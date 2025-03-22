#include "painlessMesh.h"
#include "configuration.h"
#include <Preferences.h>
#include <DHT.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink" // TODO: generate a random id for different networks
#define MESH_PASSWORD "AsterLinkMesh2025$#" // TODO: generate a random password for different networks
#define MESH_PORT 5555

// Global objects
Scheduler userScheduler;  // Task scheduler for painlessMesh. It runs asynchronous tasks.
Preferences preferences;  // Access NVM memory

// Sensor Pins
#define DHTPIN 4         // GPIO for DHT11
#define DHTTYPE DHT11
#define LIGHT_SENSOR_PIN 34   // GPIO pin for Light Sensor (LDR)
#define SOIL_MOISTURE_PIN 35  // GPIO pin for Soil Moisture Sensor

// LED Pins 
#define LED_PIN 19       // Blinks when not connected, Solid when connected

// Configuration variables
uint64_t state;
uint64_t meshInitialized;
uint64_t sentinelId;
uint64_t deviceId;

// Initialize sensors and mesh
DHT dht(DHTPIN, DHTTYPE);
painlessMesh mesh;

// Function prototypes
void sendSensorData();
void updateLEDStatus();

// Task to send sensor data every 60 seconds
Task taskSendMessage(TASK_SECOND * 60, TASK_FOREVER, &sendSensorData);

// Task to check connection status every 1 second
Task taskCheckConnection(150, TASK_FOREVER, &updateLEDStatus);

void sendSensorData() {

  // Create JSON object
  StaticJsonDocument<200> jsonDoc;

  // Read sensor values
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int raw_light = analogRead(LIGHT_SENSOR_PIN);
  int raw_soil = analogRead(SOIL_MOISTURE_PIN);

  // Ensure we have valid sensor readings
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Error: Failed to read from DHT11 sensor!");
    return;  // Don't send invalid data
  }

  // Normalize sensor values
  int light_level = map(raw_light, 0, 4095, 0, 100);
  int soil_moisture = map(raw_soil, 0, 4095, 0, 100);

  // Populate JSON object
  jsonDoc["device_id"] = deviceId;
  jsonDoc["temperature"] = temperature;
  jsonDoc["humidity"] = humidity;
  jsonDoc["light_level"] = light_level;
  jsonDoc["soil_moisture"] = soil_moisture;
  jsonDoc["instruction_type"] = SEND_SENSOR_DATA;

  // Serialize JSON to string
  String msg;
  serializeJson(jsonDoc, msg);

  // Send the message over the mesh network
  mesh.sendSingle(sentinelId, msg);

  // Print debug message
  Serial.println("Sent JSON: " + msg);
}

void updateLEDStatus() {
  int nodeCount = mesh.getNodeList().size();
  if (nodeCount > 0) {
    digitalWrite(LED_PIN, HIGH);
  } else {
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
  }
}

uint64_t macAddressToInteger(const String& mac) {
  uint64_t macInt = 0;
  
  // Remove colons from the MAC address
  String cleanedMAC = mac;
  cleanedMAC.replace(":", "");

  // Convert the cleaned MAC address to an integer
  macInt = strtoull(cleanedMAC.c_str(), NULL, 16);

  return macInt;
}

void receivedCallback(uint64_t from, String &msg) {
  Serial.printf("DEBUG: Received message from %llu: %s\n", from, msg.c_str());
  StaticJsonDocument<256> jsonDoc;
  Serial.printf("DEBUG: Deserializing received JSON message\n");
  DeserializationError error = deserializeJson(jsonDoc, msg);
  
  if (error) {
    Serial.print("JSON Parsing Failed: ");
    Serial.println(error.f_str());
    return;
  }

  // Read sensor data fields from the incoming message
  uint64_t instructionType = jsonDoc["instruction_type"];

  if (instructionType == CONFIGURE) {
    sentinelId = jsonDoc["sentinel_id"];
    preferences.putUInt("sentinel_id", sentinelId);
    
    // Update state and device Id
    state = ACTIVE;
    preferences.putUChar("state", ACTIVE);
    deviceId = macAddressToInteger(WiFi.macAddress());
    preferences.putUInt("device_id", deviceId);

    // Send CONFIGURE_DEVICE_ID instruction to sentinel device
    jsonDoc.clear();
    jsonDoc["instruction_type"] = CONFIGURE_DEVICE_ID;
    jsonDoc["device_id"] = deviceId;
    jsonDoc["password"] = "password";
    String instruction;
    serializeJson(jsonDoc, instruction);
    mesh.sendSingle(sentinelId, instruction);

    activate();
  }
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

  dht.begin();
  pinMode(LIGHT_SENSOR_PIN, INPUT);
  pinMode(SOIL_MOISTURE_PIN, INPUT);

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

  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();
  taskCheckConnection.disable();

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
