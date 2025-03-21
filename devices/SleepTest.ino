#include <Arduino.h>
#include "painlessMesh.h"
#include <DHT.h>
#include <ArduinoJson.h>

#define MESH_PREFIX "asterlink"
#define MESH_PASSWORD "AsterLinkMesh2025$#"
#define MESH_PORT 5555

#define uS_TO_S_FACTOR 1000000ULL
#define TIME_TO_SLEEP 30  // Change this for your desired sleep duration

RTC_DATA_ATTR int bootCount = 0;

#define DHTPIN 4
#define DHTTYPE DHT11
#define GREEN_LED_PIN 19
#define RED_LED_PIN 18

DHT dht(DHTPIN, DHTTYPE);
Scheduler userScheduler;
painlessMesh mesh;

void receivedCallback(uint32_t from, String &msg) {
  // Optional: Uncomment for debugging
  // Serial.printf("Received from %u: %s\n", from, msg.c_str());
}

void newConnectionCallback(uint32_t nodeId) {
  // Optional: Uncomment for debugging
  // Serial.printf("New node joined: %u\n", nodeId);
}

void changedConnectionCallback() {
  // Optional: Uncomment for debugging
  // Serial.println("Connection list changed.");
}

void nodeTimeAdjustedCallback(int32_t offset) {
  // Optional: Uncomment for debugging
  // Serial.printf("Time adjusted. Offset = %d\n", offset);
}

void initializeMeshAndSensors() {
  dht.begin();

  pinMode(GREEN_LED_PIN, OUTPUT);
  pinMode(RED_LED_PIN, OUTPUT);
  digitalWrite(GREEN_LED_PIN, LOW);
  digitalWrite(RED_LED_PIN, HIGH);

  //mesh.setDebugMsgTypes(ERROR);  // Keep logs minimal
  mesh.init(MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT);
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);

  for (int i = 0; i < 10; i++) {
    mesh.update();
    delay(200);
  }
}

void sendSensorData() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();

  Serial.print("[DHT] Temp: ");
  Serial.print(temperature);
  Serial.print("  Humidity: ");
  Serial.println(humidity);

  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("[Error] DHT read failed.");
    return;
  }

  StaticJsonDocument<150> doc;
  doc["device_id"] = mesh.getNodeId();
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;

  String msg;
  serializeJson(doc, msg);
  mesh.sendBroadcast(msg);

  Serial.println("[Data] Sent JSON");

  Serial.print("[Mesh] Node count: ");
  Serial.println(mesh.getNodeList().size());
}

void goToSleep() {
  esp_sleep_enable_timer_wakeup(TIME_TO_SLEEP * uS_TO_S_FACTOR);
  Serial.flush();
  esp_deep_sleep_start();
}

void setup() {
  Serial.begin(115200);
  delay(500);  // Enough to allow logging if needed

  ++bootCount;

  initializeMeshAndSensors();
  sendSensorData();
  goToSleep();
}

void loop() {
  // Not used
}
