#include "painlessMesh.h"
#include <WiFi.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include <vector>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555

const char* externalSSID = "test1234";
const char* externalPassword = "password123";
const char* apiEndpoint = "https://asterlink-fzgndcaefabkb0gh.eastus-01.azurewebsites.net/record/batch";

Scheduler userScheduler;
painlessMesh mesh;

std::vector<String> dataBuffer;
unsigned long lastSentTime = 0;
const unsigned long interval = 60000;
const char* ntpServer = "time.google.com";
const long gmtOffset_sec = -5;
const int daylightOffset_sec = 0;

void processReceivedData(const String& msg);

void receivedCallback(uint32_t from, String &msg) {
    Serial.printf("Received from %u: %s\n", from, msg.c_str());
    processReceivedData(msg);
}

void newConnectionCallback(uint32_t nodeId) {
    Serial.printf("New Connection, nodeId = %u\n", nodeId);
}

void changedConnectionCallback() {
    Serial.println("🔄 Mesh Network Changed!");
}

void processReceivedData(const String& msg) {
    StaticJsonDocument<256> jsonDoc;
    DeserializationError error = deserializeJson(jsonDoc, msg);

    if (error) {
        Serial.print("JSON Parsing Failed: ");
        Serial.println(error.f_str());
        return;
    }

    uint32_t device_id = jsonDoc["device_id"];
    float temperature = jsonDoc["temperature"];
    float humidity = jsonDoc["humidity"];
    int light_level = jsonDoc["light_level"];
    int soil_moisture = jsonDoc["soil_moisture"];

    Serial.println("\n--- Sensor Data Received ---");
    Serial.printf("Device ID: %u\n", device_id);
    Serial.printf("Temperature: %.2f°C\n", temperature);
    Serial.printf("Humidity: %.2f%%\n", humidity);
    Serial.printf("Light Level: %d%%\n", light_level);
    Serial.printf("Soil Moisture: %d%%\n", soil_moisture);
    Serial.println("----------------------------");

    StaticJsonDocument<256> pushDoc;
    pushDoc["device_id"] = device_id;
    pushDoc["temp"] = temperature;
    pushDoc["humidity"] = humidity;
    pushDoc["light"] = light_level;
    pushDoc["soil"] = soil_moisture;

    String pushPayload;
    serializeJson(pushDoc, pushPayload);

    dataBuffer.push_back(pushPayload); // Store the data for batch processing
}

void sendDataToAPI() {
    if (dataBuffer.empty()) {
        Serial.println("No data to send.");
        return;
    }

    Serial.println("Connecting to WiFi...");
    WiFi.begin(externalSSID, externalPassword);
    unsigned long wifiStart = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - wifiStart < 10000) { // 10s timeout
        delay(500);
        Serial.print(".");
    }
    
    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("\nFailed to connect to WiFi");
        return;
    }
    
    Serial.println("\nConnected to WiFi, sending data...");

    StaticJsonDocument<2048> batchJson; // Adjust size if needed
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
    Serial.println(batchPayload);
    HTTPClient http;
    http.begin(apiEndpoint);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(batchPayload);
    Serial.printf("HTTP Response Code: %d\n", httpResponseCode);

    if (httpResponseCode > 0) {
        Serial.println("Data sent successfully!");
        dataBuffer.clear(); // Clear the buffer after successful send
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
        return "2025-02-05T10:02:00.000Z"; // Fallback timestamp
    }

    char buffer[30];
    strftime(buffer, sizeof(buffer), "%Y-%m-%dT%H:%M:%S.000Z", &timeinfo);
    return String(buffer);
}

void setup() {
    Serial.begin(115200);
    WiFi.mode(WIFI_OFF);

    configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

    mesh.setDebugMsgTypes(ERROR | MESH_STATUS | CONNECTION |  COMMUNICATION);
    mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);

    mesh.onReceive(&receivedCallback);
    mesh.onNewConnection(&newConnectionCallback);
    mesh.onChangedConnections(&changedConnectionCallback);

    mesh.setRoot(true);
    mesh.setContainsRoot(true);

    lastSentTime = millis();
}

void loop() {
    mesh.update();
    
    if (millis() - lastSentTime >= interval) {
        sendDataToAPI();
        lastSentTime = millis();
    }
}
