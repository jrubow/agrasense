#include "painlessMesh.h"
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555
#define CONFIGURATION 1


// Wi-Fi Credentials
const char* ssid = "test1234";
const char* password = "password123";

// API Endpoint (Placeholder)
const char* API_ENDPOINT = "http://your-api-endpoint.com/data";

Scheduler userScheduler;
painlessMesh mesh;

// Function prototypes
void connectToWiFi();
void processReceivedData(const String& msg);
void sendDataToAPI(const String& jsonPayload);  // Placeholder

// Callback function when data is received
void receivedCallback(uint32_t from, String &msg) {
    Serial.printf("Received from %u: %s\n", from, msg.c_str());
    processReceivedData(msg);
}

// Callback when a new node connects
void newConnectionCallback(uint32_t nodeId) {
    Serial.printf("New Connection, nodeId = %u\n", nodeId);
}

// Callback when mesh network changes
void changedConnectionCallback() {
    Serial.println("Changed connections");
}

// Callback for time sync
void nodeTimeAdjustedCallback(int32_t offset) {
    Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(), offset);
}

// Function to connect to Wi-Fi
void connectToWiFi() {
    Serial.print("Connecting to Wi-Fi");
    WiFi.begin(ssid, password);

    int retryCount = 0;
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.print(".");
        retryCount++;

        if (retryCount > 20) {  // Timeout after 20 seconds
            Serial.println("\nFailed to connect to Wi-Fi. Rebooting...");
            ESP.restart();
        }
    }

    Serial.println("\nWi-Fi Connected!");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
}

// Function to process received sensor data
void processReceivedData(const String& msg) {
    StaticJsonDocument<200> jsonDoc;
    DeserializationError error = deserializeJson(jsonDoc, msg);

    if (error) {
        Serial.print("JSON Parsing Failed: ");
        Serial.println(error.f_str());
        return;
    }

    // Extract values from JSON
    uint32_t device_id = jsonDoc["device_id"];
    uint32_t timestamp = jsonDoc["timestamp"];
    float temperature = jsonDoc["temperature"];
    float humidity = jsonDoc["humidity"];
    int light_level = jsonDoc["light_level"];
    int soil_moisture = jsonDoc["soil_moisture"];

    // Print data to Serial Monitor
    Serial.println("\n--- Sensor Data Received ---");
    Serial.printf("Device ID: %u\n", device_id);
    Serial.printf("Timestamp: %u\n", timestamp);
    Serial.printf("Temperature: %.2f°C\n", temperature);
    Serial.printf("Humidity: %.2f%%\n", humidity);
    Serial.printf("Light Level: %d%%\n", light_level);
    Serial.printf("Soil Moisture: %d%%\n", soil_moisture);
    Serial.println("----------------------------");

    // TODO: Future Implementation - Send data to API
    // sendDataToAPI(msg);
}

// Placeholder function for API integration
void sendDataToAPI(const String& jsonPayload) {
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("Wi-Fi not connected. Cannot send data.");
        return;
    }

    HTTPClient http;
    http.begin(API_ENDPOINT);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(jsonPayload);
    Serial.printf("API Response: %d\n", httpResponseCode);

    http.end();
}

void setup() {
    Serial.begin(115200);
    
    // Connect to Wi-Fi
    connectToWiFi();

    // Initialize mesh network
    mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | SYNC | COMMUNICATION | GENERAL | MSG_TYPES | REMOTE);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

    // Set callbacks
    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);
    mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
}

void loop() {
    mesh.update();
}
