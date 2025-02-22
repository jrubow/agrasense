#include "painlessMesh.h"
#include <DHT.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555
#define CONFIGURATION 1

Scheduler userScheduler; // Task scheduler for painlessMesh

// Sensor Pins
#define DHTPIN 4  // GPIO for DHT11
#define DHTTYPE DHT11
#define LIGHT_SENSOR_PIN 0  // GPIO pin for Light Sensor (LDR)
#define SOIL_MOISTURE_PIN 35 // GPIO pin for Soil Moisture Sensor

// Initialize DHT Sensor
DHT dht(DHTPIN, DHTTYPE);
painlessMesh mesh;

// Function prototype
void sendSensorData();

// Task to send sensor data every 10 seconds
Task taskSendMessage(TASK_SECOND * 10, TASK_FOREVER, &sendSensorData);

void sendSensorData() {
    // Create JSON object
    StaticJsonDocument<200> jsonDoc;

    // Read sensor values
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();
    int raw_light = analogRead(LIGHT_SENSOR_PIN);
    int raw_soil = analogRead(SOIL_MOISTURE_PIN);
    uint32_t device_id = mesh.getNodeId();
    uint32_t timestamp = mesh.getNodeTime() / 1000; // Convert to seconds

    // Ensure we have valid sensor readings
    if (isnan(temperature) || isnan(humidity)) {
        Serial.println("Error: Failed to read from DHT11 sensor!");
        return; // Don't send invalid data
    }

    // Ensure valid timestamp
    if (timestamp == 0) {
        Serial.println("Warning: Timestamp not available yet.");
        timestamp = millis() / 1000; // Use device uptime as fallback
    }

    // Normalize light level & soil moisture (convert 0-4095 to 0-100%)
    int light_level = map(raw_light, 0, 4095, 0, 100);
    int soil_moisture = map(raw_soil, 0, 4095, 0, 100);

    // Populate JSON object
    jsonDoc["device_id"] = device_id;
    jsonDoc["timestamp"] = timestamp;
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

// Callbacks for painlessMesh
void receivedCallback(uint32_t from, String &msg) {
    Serial.printf("Received from %u msg=%s\n", from, msg.c_str());
}

void newConnectionCallback(uint32_t nodeId) {
    Serial.printf("New Connection, nodeId = %u\n", nodeId);
}

void changedConnectionCallback() {
    Serial.println("Changed connections");
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

    // Initialize mesh network
    mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | SYNC | COMMUNICATION | GENERAL | MSG_TYPES | REMOTE);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);
    
    // Set callbacks
    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectio
