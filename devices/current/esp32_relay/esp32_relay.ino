#include "painlessMesh.h"
#include "configuration.h"
#include <Preferences.h>
#include <DHT.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink" //TODO generate a random id for different networks
#define MESH_PASSWORD "AsterLinkMesh2025$#" // TODO generate a random password for different networks
#define MESH_PORT 5555

Scheduler userScheduler;  // Task scheduler for painlessMesh. It runs asynchronous tasks.
Preferences preferences;  // Access NVM memory

// Sensor Pins
#define DHTPIN 4  // GPIO for DHT11
#define DHTTYPE DHT11
#define LIGHT_SENSOR_PIN 34   // GPIO pin for Light Sensor (LDR)
#define SOIL_MOISTURE_PIN 35  // GPIO pin for Soil Moisture Sensor

// LED Pins 
#define LED_PIN 19 // Blinks when not connected, Solid when connected

// Configuration
uint64_t state;
uint64_t meshInitialized;
uint64_t sentinelId;
uint64_t deviceId;


// Initialize DHT Sensor and painlessMesh
DHT dht(DHTPIN, DHTTYPE);
painlessMesh mesh;

// Function prototype - syntax
void sendSensorData();
void updateLEDStatus();

// Task to send sensor data every 60 seconds
Task taskSendMessage(TASK_SECOND * 60, TASK_FOREVER, &sendSensorData);

// Task to check connection status every 1 seconds
Task taskCheckConnection(150, TASK_FOREVER, &updateLEDStatus);


void sendSensorData() {
  //Serial.print("Node Count: ");
  //Serial.println(mesh.getNodeList().size());

  // TODO - create our own template
  
  // Create JSON object
  StaticJsonDocument<200> jsonDoc;

  // Read sensor values
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int raw_light = analogRead(LIGHT_SENSOR_PIN);
  int raw_soil = analogRead(SOIL_MOISTURE_PIN);
  uint32_t device_id = mesh.getNodeId();

  // Ensure we have valid sensor readings
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Error: Failed to read from DHT11 sensor!");
    return;  // Don't send invalid data
  }

  // Normalize light level & soil moisture (convert 0-4095 to 0-100%)
  int light_level = map(raw_light, 0, 4095, 0, 100);
  int soil_moisture = map(raw_soil, 0, 4095, 0, 100);

  // Populate JSON object
  jsonDoc["device_id"] = device_id;
  jsonDoc["temperature"] = temperature;
  jsonDoc["humidity"] = humidity;
  jsonDoc["light_level"] = light_level;
  jsonDoc["soil_moisture"] = soil_moisture;
  jsonDoc["instruction_type"] = SEND_SENSOR_DATA;

  // Serialize JSON to string
  String msg;
  serializeJson(jsonDoc, msg);

  // Send the message over the mesh network to all nodes without looking at their IDs
  mesh.sendSingle(sentinelId, msg);

  // TODO - look into sendSingle()

  // Print debug message
  Serial.println("Sent JSON: " + msg);
}

// Function to update LED status based on connectivity
void updateLEDStatus() {
  int nodeCount = mesh.getNodeList().size();
  if (nodeCount > 0) {
    digitalWrite(LED_PIN, HIGH);
  } else {
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
  }
}

// Callbacks for painlessMesh

//triggered when a node recieves a message
uint64_t macAddressToInteger(const String& mac) {
    uint64_t macInt = 0;
    
    // Remove colons from the MAC address
    String cleanedMAC = mac;
    cleanedMAC.replace(":", "");

    // Convert the cleaned MAC address (hex string) to an integer
    macInt = strtoull(cleanedMAC.c_str(), NULL, 16);
    
    return macInt;
}

void receivedCallback(uint32_t from, String &msg) {
  Serial.printf("Received from %u msg=%s\n", from, msg.c_str());
  StaticJsonDocument<256> jsonDoc;
  DeserializationError error = deserializeJson(jsonDoc, msg);
  
  if (error) {
    Serial.print("JSON Parsing Failed: ");
    Serial.println(error.f_str());
    return;
  }

  // Read sensor data fields from the incoming message
  uint32_t instructionType = jsonDoc["instruction_type"];

  if (instructionType == CONFIGURE) {
    sentinelId = jsonDoc["sentinel_id"];
    preferences.putUInt("sentinel_id", sentinelId);
    

    // Update state and device Id
    state = ACTIVE;
    preferences.putUChar("state", ACTIVE);
    deviceId = macAddressToInteger(WiFi.macAddress());
    preferences.putUInt("device_id", deviceId);

    // CONFIGURE_DEVICE_ID instruction to sentinel device
    jsonDoc.clear();
    jsonDoc["instruction_type"] = CONFIGURE_DEVICE_ID;
    jsonDoc["device_id"] = deviceId;
    String instruction;
    serializeJson(jsonDoc, instruction);
    mesh.sendSingle(sentinelId, instruction);

    activate();
  }
}

// Triggered when a new node connects to the mesh
void newConnectionCallback(uint64_t nodeId) {
  Serial.printf("New Connection, nodeId = %u\n", nodeId);
  
}

// Triggered when there are any changes in mesh connections (e.g., node joins/leaves).
void changedConnectionCallback() {
  Serial.println("Changed connections");
}

// TODO what does this do? And why do we need it?
void nodeTimeAdjustedCallback(uint64_t offset) {
  Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(), offset);
}

int initialize() {
  // Initialize LEDs
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  // Enables LED Pin
  userScheduler.addTask(taskCheckConnection);
  taskCheckConnection.enable();

  // Initialize mesh network
  mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | GENERAL);
  mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

  // Set callbacks
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
  
  meshInitialized = 1;
  
  return 0;
}

int activate() {

  Serial.printf("ACTIVATED\n");
  // Initialize sensors
  dht.begin();
  pinMode(LIGHT_SENSOR_PIN, INPUT);
  pinMode(SOIL_MOISTURE_PIN, INPUT);

  // Turn off LED
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  if (!meshInitialized) {
    // Initialize mesh network
    mesh.setDebugMsgTypes(GENERAL);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

    // Set callbacks
    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);
    mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
    meshInitialized = 1;
  }

  // Enable sensor data sending task
  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();
  taskCheckConnection.disable();

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