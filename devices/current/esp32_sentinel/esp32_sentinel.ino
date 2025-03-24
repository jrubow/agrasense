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
const char* batchApiEndpoint = "https://asterlink-fzgndcaefabkb0gh.eastus-01.azurewebsites.net/api/record/batch";
const char* initApiEndpoint = "https://asterlink-fzgndcaefabkb0gh.eastus-01.azurewebsites.net/api/devices/sentinel/initialize";
const char* initRelayApiEndpoint = "https://asterlink-fzgndcaefabkb0gh.eastus-01.azurewebsites.net/api/devices/relay/initialize/batch";

Scheduler userScheduler;
Preferences preferences;
painlessMesh mesh;

std::vector<String> dataBuffer;
std::vector<String> initBuffer;
unsigned long lastSentTime = 0;
const unsigned long interval = 60000;  // 60 seconds interval
const char* ntpServer = "time.google.com";
const long gmtOffset_sec = -5;
const int daylightOffset_sec = 0;

// Configuration Variables
uint64_t state;
uint64_t deviceId;
uint64_t meshInitialized;
uint64_t sentinelId;


// Global variable to store registered device id from initialization response
uint64_t registeredDeviceId = 0;

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
        

        // Format JSON Temperature data
        StaticJsonDocument<256> pushDoc;
        pushDoc["device_id"] = sensorDeviceId;
        pushDoc["type"] = TEMPERATURE;
        pushDoc["value"] = temperature;
        String pushPayload;
        serializeJson(pushDoc, pushPayload);
        dataBuffer.push_back(pushPayload);

        // Format JSON Humidity data
        serializeJson(pushDoc, pushPayload);
        pushDoc["type"] = HUMIDITY;
        pushDoc["value"] = humidity;
        dataBuffer.push_back(pushPayload)
    } else if (instructionType == CONFIGURE_DEVICE_ID) {
      Serial.printf("Received CONFIGURE_DEVICE_ID : %d\n", jsonDoc["device_id"]);
      jsonDoc.remove("instruction_type");
      String pushDevice;
      serializeJson(jsonDoc, pushDevice);
      initBuffer.push_back(pushDevice);
    }
}

void sendDataToAPI() {

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
    HTTPClient http;
    String batchPayload;
    
    if (!dataBuffer.empty()) {
      String timestamp = getFormattedTime();
      for (const auto& entry : dataBuffer) {
          StaticJsonDocument<256> tempDoc;
          deserializeJson(tempDoc, entry);
          // tempDoc["timestamp"] = timestamp;
          jsonArray.add(tempDoc);
      }

      
      serializeJson(batchJson, batchPayload);
      Serial.println("Batch Payload:");
      Serial.println(batchPayload);

      
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
    } else {
      Serial.println("No data to send.");
    }

    if (!initBuffer.empty()) {
        http.begin(initRelayApiEndpoint);
        http.addHeader("X-API-KEY", "user");
        http.addHeader("Content-Type", "application/json");

        batchJson.clear();
        jsonArray = batchJson.to<JsonArray>();
        for (const auto& entry : initBuffer) {
            StaticJsonDocument<256> tempDoc;
            deserializeJson(tempDoc, entry);
            tempDoc["sentinel_id"] = sentinelId;
            jsonArray.add(tempDoc);
        }

        serializeJson(batchJson, batchPayload);
        Serial.println("Batch Payload:");
        Serial.println(batchPayload);

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
    } else {
        Serial.printf("No devices to init\n");
    }

    initBuffer.clear();
    dataBuffer.clear();
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

uint64_t macAddressToInteger(const String& mac) {
    uint64_t macInt = 0;
    
    // Remove colons from the MAC address
    String cleanedMAC = mac;
    cleanedMAC.replace(":", "");

    // Convert the cleaned MAC address (hex string) to an integer
    macInt = strtoull(cleanedMAC.c_str(), NULL, 16);
    
    return macInt;
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
    sentinelId = macAddressToInteger(WiFi.macAddress());
    initDoc["device_id"] = sentinelId;
    initDoc["password"] = "password";
    
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
