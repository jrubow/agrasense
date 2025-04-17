#include "painlessMesh.h"
#include "configuration.h"
#include <Preferences.h>
#include <DHT.h>
#include <ArduinoJson.h>
#include <sys/time.h>
#include "InstructionHandler.hh"

#define DEBUG

#define MESH_PREFIX "asterlink" // TODO: generate a random id for different networks
#define MESH_PASSWORD "AsterLinkMesh2025$#" // TODO: generate a random password for different networks
#define MESH_PORT 5555

// Global objects
Scheduler userScheduler;  // Task scheduler for painlessMesh. It runs asynchronous tasks.
Preferences preferences;  // Access NVM memory
timeval tv;               // RTC access

// LED Pins 
#define LED_PIN 19       // Blinks when not connected, Solid when connected

// Configuration variables
unsigned char state;
uint64_t meshInitialized;
uint64_t sentinelId;
uint64_t deviceId;

// Function prototypes
void updateLEDStatus();
int activate();
int initialize();

// Initialize sensors and mesh
painlessMesh mesh;
InstructionHandler instructions(tv, deviceId, sentinelId, preferences, activate, mesh, state);



// Task to check connection status every 1 second
Task taskCheckConnection(150, TASK_FOREVER, &updateLEDStatus);

void updateLEDStatus() {
  int nodeCount = mesh.getNodeList().size();
  if (nodeCount > 0) {
    digitalWrite(LED_PIN, HIGH);
  } else {
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
  }
}

void receivedCallback(uint64_t from, String &msg) {
  instructions.executeInstruction(std::string(msg.c_str()));
}

void newConnectionCallback(uint64_t nodeId) {
  Serial.printf("DEBUG: New Connection, nodeId = %llu\n", nodeId);
}

void changedConnectionCallback() {
  Serial.println("Changed connections");
}

void nodeTimeAdjustedCallback(uint64_t offset) {
  Serial.printf("DEBUG: Adjusted time %llu. Offset = %d\n", mesh.getNodeTime(), offset);
}

int initialize() {
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  userScheduler.addTask(taskCheckConnection);
  taskCheckConnection.enable();

  mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | GENERAL);
  mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);

  meshInitialized = 1;

  return 0;
}

int activate() {
  Serial.printf("ACTIVATED\n");

  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  if (!meshInitialized) {
    mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | GENERAL);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);
    mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
    meshInitialized = 1;
  }

  taskCheckConnection.disable();

  instructions.executeSpecifiedInstruction(SEND_SENSOR_DATA);

  Serial.printf("DEBUG: Exiting activate() with return value 1\n");
  return 1;
}

void setup() {
  Serial.begin(115200);

  preferences.begin("configuration", false);
  preferences.putUChar("state", INIT);
  state = preferences.getUChar("state", INIT);

  if (state == INIT) {
    Serial.printf("INIT\n");
    preferences.putUChar("state", INIT);
    state = INIT;
    initialize();
  } else if (state == ACTIVE) {
    deviceId = preferences.getUInt("device_id");
    sentinelId = preferences.getUInt("sentinel_id");
    activate();
  }
}

void loop() {
  mesh.update();
}
