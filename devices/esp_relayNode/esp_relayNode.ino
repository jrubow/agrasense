#include "painlessMesh.h"
#include <DHT.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555

Scheduler userScheduler;  // Task scheduler for painlessMesh

// Sensor Pins
#define DHTPIN 4  // GPIO for DHT11
#define DHTTYPE DHT11
#define LIGHT_SENSOR_PIN 34   // GPIO pin for Light Sensor (LDR)
#define SOIL_MOISTURE_PIN 35  // GPIO pin for Soil Moisture Sensor

// LED Pins
#define GREEN_LED_PIN 19  // Green LED - ON when connected
#define RED_LED_PIN 18    // Red LED - ON when disconnected

// Initialize DHT Sensor
DHT dht(DHTPIN, DHTTYPE);
painlessMesh mesh;

// Function prototype
void sendSensorData();
void updateLEDStatus();

// Task to send sensor data every 1 seconds
Task taskSendMessage(TASK_SECOND * 1, TASK_FOREVER, &sendSensorData);

// Task to check connection status every 1 seconds
Task taskCheckConnection(TASK_SECOND * 1, TASK_FOREVER, &updateLEDStatus);


void sendSensorData() {
  Serial.print("Node Count: ");
  Serial.println(mesh.getNodeList().size());


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

  // Serialize JSON to string
  String msg;
  serializeJson(jsonDoc, msg);

  // Send the message over the mesh network
  mesh.sendBroadcast(msg);

  // Print debug message
  Serial.println("Sent JSON: " + msg);
}

// Function to update LED status based on connectivity
void updateLEDStatus() {
  int nodeCount = mesh.getNodeList().size();
  if (nodeCount > 0) {
    // if it is connected
    digitalWrite(GREEN_LED_PIN, HIGH);
    digitalWrite(RED_LED_PIN, LOW); 
  } else {
    // if not connected
    digitalWrite(GREEN_LED_PIN, LOW);
    digitalWrite(RED_LED_PIN, HIGH);
  }
}

// Callbacks for painlessMesh
void receivedCallback(uint32_t from, String &msg) {
  Serial.printf("Received from %u msg=%s\n", from, msg.c_str());
}

void newConnectionCallback(uint32_t nodeId) {
  Serial.printf("New Connection, nodeId = %u\n", nodeId);
  updateLEDStatus();
}

void changedConnectionCallback() {
  Serial.println("Changed connections");
  updateLEDStatus();
}

void nodeTimeAdjustedCallback(int32_t offset) {
  Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(), offset);
}

void setup() {
  Serial.begin(115200);

  // Initialize sensors
  dht.begin();
  pinMode(LIGHT_SENSOR_PIN, INPUT);
  pinMode(SOIL_MOISTURE_PIN, INPUT);

  // Initialize LEDs
  pinMode(GREEN_LED_PIN, OUTPUT);
  pinMode(RED_LED_PIN, OUTPUT);
  digitalWrite(GREEN_LED_PIN, HIGH);  // Force the Green LED ON for testing
  digitalWrite(RED_LED_PIN, LOW);     // Ensure the Red LED is OFF

  // Initialize mesh network
  mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | GENERAL);
  mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

  // Set callbacks
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);

  // Enable sensor data sending task
  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();
  userScheduler.addTask(taskCheckConnection);
  taskCheckConnection.enable();
}

void loop() {
  mesh.update();
}
