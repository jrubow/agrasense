#include "painlessMesh.h"
#include "configuration.h"
#include <Preferences.h>
#include <WiFi.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include <vector>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555

const char* externalSSID = "test1234";
const char* externalPassword = "password123";

// API endpoints
const char* batchApiEndpoint = "https://anda-ate6apf9cec3czb6.centralus-01.azurewebsites.net/api/reports/batch";
const char* initApiEndpoint = "https://anda-ate6apf9cec3czb6.centralus-01.azurewebsites.net/api/devices/sentinel/initialize";

Scheduler userScheduler;
Preferences preferences;
painlessMesh mesh;

std::vector<String> dataBuffer;
unsigned long lastSentTime = 0;
const unsigned long interval = 60000;  // 60 seconds interval
const char* ntpServer = "time.google.com";
const long gmtOffset_sec = -5;
const int daylightOffset_sec = 0;

// Configuration Variables
int state;
int deviceId;
int meshInitialized;

// Global variable to store registered device id from initialization response
uint32_t registeredDeviceId = 0;

void processReceivedData(const String& msg);

void receivedCallback(uint32_t from, String &msg) {
    Serial.printf("Received from %u: %s\n", from, msg.c_str());
    processReceivedData(msg);
}

void newConnectionCallback(uint32_t nodeId) {
    // TODO - need to keep track of devices that have already been added, for now the below will work
    //        an array of ints of nodeIds stored in flashram should work, careful flash only has 10,000 writes

    Serial.printf("New Connection, nodeId = %u\n", nodeId);
    StaticJsonDocument<256> doc;
    doc["instruction_type"] = CONFIGURE;
    doc["sentinel_id"] = mesh.getNodeId();
    
    // Send SET_SENTINEL_ID message to new device
    String output;
    serializeJson(doc, output);
    mesh.sendSingle(nodeId, output);
}

void changedConnectionCallback() {
    Serial.println("Mesh Network Changed!");
}

void processReceivedData(const String& msg) {
    StaticJsonDocument<256> jsonDoc;
    DeserializationError error = deserializeJson(jsonDoc, msg);

    if (error) {
        Serial.print("JSON Parsing Failed: ");
        Serial.println(error.f_str());
        return;
    }

    uint64_t instructionType = jsonDoc["instruction_type"];
    if (instructionType == SEND_SENSOR_DATA) {// Read sensor data fields from the incoming message
      uint64_t sensorDeviceId = jsonDoc["device_id"];
      float temperature = jsonDoc["temperature"];
      float humidity = jsonDoc["humidity"];
      int light_level = jsonDoc["light_level"];
      int soil_moisture = jsonDoc["soil_moisture"];

      Serial.println("\n--- Sensor Data Received ---");
      Serial.printf("Sensor Device ID: %u\n", sensorDeviceId);
      Serial.printf("Temperature: %.2f°C\n", temperature);
      Serial.printf("Humidity: %.2f%%\n", humidity);
      Serial.printf("Light Level: %d%%\n", light_level);
      Serial.printf("Soil Moisture: %d%%\n", soil_moisture);
      Serial.println("----------------------------");
      float temperatureF = temperature * 9.0 / 5.0 + 32.0;

      // Create JSON report for temperature reading in the new batch format
      StaticJsonDocument<256> pushDoc;
      // Use the registered device id (if set) instead of the sensor's device id.
      uint32_t reportDeviceId = (registeredDeviceId != 0) ? registeredDeviceId : sensorDeviceId;
      pushDoc["device_id"] = reportDeviceId;
      pushDoc["report_type"] = "TEMP";
      pushDoc["value"] = temperatureF;
      pushDoc["units"] = "Fahrenheit";

      String pushPayload;
      serializeJson(pushDoc, pushPayload);

      dataBuffer.push_back(pushPayload);
    } else if (instructionType == CONFIGURE_DEVICE_ID) {
      Serial.printf("Received CONFIGURE_DEVICE_ID : %d\n", jsonDoc["device_id"]);
    }
}

void sendDataToAPI() {
    if (dataBuffer.empty()) {
        Serial.println("No data to send.");
        return;
    }

    Serial.println("Connecting to WiFi...");
    WiFi.begin(externalSSID, externalPassword);
    unsigned long wifiStart = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - wifiStart < 10000) {
        delay(500);
        Serial.print(".");
    }
    
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("\nFailed to connect to WiFi");
        return;
    }
    
    Serial.println("\nConnected to WiFi, sending batch data...");

    // Build batch JSON array payload
    StaticJsonDocument<2048> batchJson;
    JsonArray jsonArray = batchJson.to<JsonArray>();
    String timestamp = getFormattedTime();
    for (const auto& entry : dataBuffer) {
        StaticJsonDocument<256> tempDoc;
        deserializeJson(tempDoc, entry);
        tempDoc["timestamp"] = timestamp;
        jsonArray.add(tempDoc);
    }

    String batchPayload;
    serializeJson(batchJson, batchPayload);
    Serial.println("Batch Payload:");
    Serial.println(batchPayload);

    HTTPClient http;
    http.begin(batchApiEndpoint);
    http.addHeader("X-API-KEY", "user");
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(batchPayload);
    Serial.printf("HTTP Response Code: %d\n", httpResponseCode);

    if (httpResponseCode > 0) {
        Serial.println("Data sent successfully!");
        dataBuffer.clear();
    } else {
        Serial.println("Failed to send data.");
    }

    http.end();
    WiFi.disconnect(true);
    Serial.println("Disconnected from WiFi.");
}

String getFormattedTime() {
    struct tm timeinfo;
    if (!getLocalTime(&timeinfo)) {
        Serial.println("Failed to obtain time");
        return "2025-02-05T10:02:00.000Z";
    }

    char buffer[30];
    strftime(buffer, sizeof(buffer), "%Y-%m-%dT%H:%M:%S.000Z", &timeinfo);
    return String(buffer);
}

void initializeDevice() {
    Serial.println("Initializing device...");
    WiFi.begin(externalSSID, externalPassword);
    unsigned long wifiStart = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - wifiStart < 10000) {
        delay(500);
        Serial.print(".");
    }
    
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("\nFailed to connect to WiFi for initialization");
        return;
    }
    
    StaticJsonDocument<256> initDoc;
    initDoc["device_id"] = mesh.getNodeId();
    
    String initPayload;
    serializeJson(initDoc, initPayload);
    Serial.println("Initialization Payload:");
    Serial.println(initPayload);

    HTTPClient http;
    http.begin(initApiEndpoint);
    http.addHeader("X-API-KEY", "user");
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(initPayload);
    Serial.printf("Initialization HTTP Response Code: %d\n", httpResponseCode);

    if (httpResponseCode > 0) {
        String initResponse = http.getString();
        Serial.println("Initialization Response: " + initResponse);
        // Expected format: "SENTINEL DEVICE REGISTERED 5"
        int lastSpace = initResponse.lastIndexOf(' ');
        if (lastSpace >= 0) {
            String idStr = initResponse.substring(lastSpace + 1);
            // registeredDeviceId = idStr.toInt();
            registeredDeviceId = 9;
            Serial.printf("Registered Device ID: %u\n", registeredDeviceId);
        } else {
            Serial.println("Failed to parse device ID from init response");
        }
        Serial.println("Initialization successful!");
    } else {
        Serial.println("Initialization failed.");
    }

    http.end();
    WiFi.disconnect(true);
    Serial.println("Disconnected from WiFi (initialization).");
}

void setup() {
    Serial.begin(115200);
    WiFi.mode(WIFI_OFF);
    configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

    // mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION | COMMUNICATION);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);

    mesh.setRoot(true);
    mesh.setContainsRoot(true);

    initializeDevice();

    lastSentTime = millis();
}

void loop() {
    mesh.update();
    
    if (millis() - lastSentTime >= interval) {
        sendDataToAPI();
        lastSentTime = millis();
    }
}
