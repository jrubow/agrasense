#include "painlessMesh.h"
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555
#define CONFIGURATION 1

// Wi-Fi Credentials
const char* ssid = "test1234";
const char* password = "password123";

// API Endpoint (Updated to match your requirement)
const char* API_ENDPOINT = "http://192.168.137.1:8080/record";

// LCD Setup (I2C Address: 0x27 or 0x3F)
LiquidCrystal_I2C lcd(0x27, 16, 2); // 16x2 LCD, I2C Address (Check with I2C scanner)

Scheduler userScheduler;
painlessMesh mesh;

// Function prototypes
void connectToWiFi();
void checkWiFiConnection();
void processReceivedData(const String& msg);
void sendDataToAPI(const String& jsonPayload);  // Sends the JSON via POST
void updateLCD(); // Updates LCD with the number of connected nodes

// ✅ Task to check Wi-Fi connection every 15 seconds
Task taskCheckWiFi(TASK_SECOND * 15, TASK_FOREVER, &checkWiFiConnection);
// ✅ Task to update LCD every 3 seconds
Task taskUpdateLCD(TASK_SECOND * 3, TASK_FOREVER, &updateLCD);

// Callback function when data is received from a relay node
void receivedCallback(uint32_t from, String &msg) {
    Serial.printf("Received from %u: %s\n", from, msg.c_str());
    processReceivedData(msg);

    // ✅ Send an acknowledgment back to the relay
    String ackMsg = "ACK from Provisioner to Node " + String(from);
    mesh.sendSingle(from, ackMsg);
}

// Callback when a new node connects
void newConnectionCallback(uint32_t nodeId) {
    Serial.printf("New Connection, nodeId = %u\n", nodeId);
}

// Callback when the mesh network changes
void changedConnectionCallback() {
    Serial.println("🔄 Mesh Network Changed!");
    Serial.print("📡 Connected Nodes: ");
    std::list<uint32_t> nodes = mesh.getNodeList();
    for (uint32_t node : nodes) {
        Serial.printf("%u ", node);
    }
    Serial.println();
    updateLCD(); // Refresh LCD when network changes
}

// Callback for time synchronization events
void nodeTimeAdjustedCallback(int32_t offset) {
    Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(), offset);
}

// Function to check Wi-Fi connection and attempt reconnection if needed
void checkWiFiConnection() {
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("⚠️ Wi-Fi disconnected! Attempting to reconnect...");
        WiFi.disconnect(true);
        delay(1000);
        WiFi.begin(ssid, password);
        
        int retryCount = 0;
        while (WiFi.status() != WL_CONNECTED && retryCount < 10) {
            delay(500);
            Serial.print(".");
            retryCount++;
        }
        
        if (WiFi.status() == WL_CONNECTED) {
            Serial.println("\n✅ Wi-Fi Reconnected!");
            Serial.print("📡 IP Address: ");
            Serial.println(WiFi.localIP());
        } else {
            Serial.println("\n❌ Wi-Fi Reconnection Failed! Retrying in 15 seconds...");
        }
    } else {
        Serial.println("✅ Wi-Fi is still connected.");
    }
}

// Function to initially connect to Wi-Fi
void connectToWiFi() {
    Serial.print("Connecting to Wi-Fi");
    WiFi.mode(WIFI_STA);
    WiFi.disconnect();
    delay(500);
    WiFi.begin(ssid, password);
    
    int retryCount = 0;
    while (WiFi.status() != WL_CONNECTED && retryCount < 20) {
        delay(500);
        Serial.print(".");
        retryCount++;
    }
    
    if (WiFi.status() == WL_CONNECTED) {
        Serial.println("\n✅ Wi-Fi Connected!");
        Serial.print("📡 IP Address: ");
        Serial.println(WiFi.localIP());
    } else {
        Serial.println("\n❌ Wi-Fi Connection Failed. Retrying in background...");
    }
}

// Function to process received sensor data and push it to the API
void processReceivedData(const String& msg) {
    StaticJsonDocument<200> jsonDoc;
    DeserializationError error = deserializeJson(jsonDoc, msg);
    
    if (error) {
        Serial.print("JSON Parsing Failed: ");
        Serial.println(error.f_str());
        return;
    }
    
    // Extract sensor values from the received JSON
    uint32_t device_id = jsonDoc["device_id"];
    float temperature = jsonDoc["temperature"];
    float humidity = jsonDoc["humidity"];
    int light_level = jsonDoc["light_level"];
    int soil_moisture = jsonDoc["soil_moisture"];
    
    // Print the received sensor data to Serial Monitor
    Serial.println("\n--- Sensor Data Received ---");
    Serial.printf("Device ID: %u\n", device_id);
    Serial.printf("Temperature: %.2f°C\n", temperature);
    Serial.printf("Humidity: %.2f%%\n", humidity);
    Serial.printf("Light Level: %d%%\n", light_level);
    Serial.printf("Soil Moisture: %d%%\n", soil_moisture);
    Serial.println("----------------------------");
    
    // Build the JSON payload following the required schema:
    // {
    //   "record_id": 1002,
    //   "device_id": 502,
    //   "timestamp": "2025-02-22T16:10:00Z",
    //   "temp": 21.3,
    //   "humidity": 65,
    //   "light": 900,
    //   "soil": 40
    // }
    StaticJsonDocument<256> pushDoc;
    pushDoc["record_id"] = 1002;             // You may later update this dynamically if needed.
    pushDoc["device_id"] = device_id;          // Using the device_id received.
    pushDoc["timestamp"] = "2025-02-22T16:10:00Z"; // Placeholder timestamp.
    pushDoc["temp"] = temperature;             // Map temperature to "temp"
    pushDoc["humidity"] = humidity;
    pushDoc["light"] = light_level;            // Map light_level to "light"
    pushDoc["soil"] = soil_moisture;           // Map soil_moisture to "soil"
    
    String pushPayload;
    serializeJson(pushDoc, pushPayload);
    
    // Send the POST request to the API endpoint
    sendDataToAPI(pushPayload);
}

// Placeholder function for API integration (HTTP POST)
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
    
    if (httpResponseCode > 0) {
        String response = http.getString();
        Serial.println("Server Response: " + response);
    } else {
        Serial.println("Failed to send data!");
    }
    
    http.end();
}

// Update the LCD with the number of connected nodes
void updateLCD() {
    int nodeCount = mesh.getNodeList().size();
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Nodes: ");
    lcd.print(nodeCount);
}

void setup() {
    Serial.begin(115200);
    
    // Connect to Wi-Fi
    connectToWiFi();
    
    // Initialize mesh network
    mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | SYNC | COMMUNICATION | GENERAL | MSG_TYPES | REMOTE);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);
    
    // Set mesh callbacks
    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);
    mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
    
    // Add Wi-Fi check task to scheduler
    userScheduler.addTask(taskCheckWiFi);
    taskCheckWiFi.enable();
    userScheduler.addTask(taskUpdateLCD);
    taskUpdateLCD.enable();
}

void loop() {
    mesh.update();
}
