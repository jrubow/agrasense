#include <Arduino.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1309.h>
#include <ArduinoJson.h>
#include <time.h>

// OLED Display (I2C SSD1309 - 128x64)
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET     -1
Adafruit_SSD1309 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// Screen states
enum ScreenState { SCREEN_DEFAULT, SCREEN_NODE_DATA, SCREEN_ALERTS };
ScreenState currentScreen = SCREEN_DEFAULT;

// Timing for screen rotation
unsigned long lastScreenChange = 0;
const unsigned long screenInterval = 5000; // 5 seconds

// Global display data
String lastReceivedTimestamp = "--:--:--";
int meshNodeCount = 0;
bool wifiConnected = true;

float lastTemp = 0.0;
float lastHumidity = 0.0;
String lastNodeTime = "--:--:--";
String lastNodeId = "----";

String getCurrentTimeString() {
  time_t now = time(nullptr);
  struct tm* timeinfo = localtime(&now);
  char buffer[16];
  strftime(buffer, sizeof(buffer), "%H:%M:%S", timeinfo);
  return String(buffer);
}

void drawDefaultScreen() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.println("AsterLink Sentinel");
  display.println(wifiConnected ? "Wi-Fi: Connected" : "Wi-Fi: Disconnected");

  display.print("Nodes: ");
  display.println(meshNodeCount);

  display.print("Last RX: ");
  display.println(lastReceivedTimestamp);
  display.display();
}

void drawNodeDataScreen() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);

  display.print("Node: ");
  display.println(lastNodeId);
  display.print("Temp: ");
  display.print(lastTemp); display.println(" C");
  display.print("Humidity: ");
  display.print(lastHumidity); display.println(" %");
  display.print("Time: ");
  display.println(lastNodeTime);

  display.display();
}

void drawAlertScreen() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);

  display.println("! ALERTS");
  if (!wifiConnected) {
    display.println("Wi-Fi Disconnected!");
  } else {
    display.println("All systems OK.");
  }
  display.display();
}

void updateScreen() {
  unsigned long now = millis();
  if (now - lastScreenChange > screenInterval) {
    currentScreen = static_cast<ScreenState>((static_cast<int>(currentScreen) + 1) % 3);
    lastScreenChange = now;
  }

  switch (currentScreen) {
    case SCREEN_DEFAULT:
      drawDefaultScreen();
      break;
    case SCREEN_NODE_DATA:
      drawNodeDataScreen();
      break;
    case SCREEN_ALERTS:
      drawAlertScreen();
      break;
  }
}

void processReceivedData(const String& msg) {
  StaticJsonDocument<256> doc;
  DeserializationError err = deserializeJson(doc, msg);
  if (err) {
    Serial.println("JSON parse failed!");
    return;
  }

  lastNodeId = String((uint32_t)doc["device_id"], HEX);
  lastTemp = doc["temperature"];
  lastHumidity = doc["humidity"];
  lastNodeTime = getCurrentTimeString();
  lastReceivedTimestamp = lastNodeTime;
}

void setup() {
  Serial.begin(115200);
  if (!display.begin(SSD1309, 0x3C)) { // Default I2C address
    Serial.println("SSD1309 allocation failed");
    for (;;);
  }
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 20);
  display.println("Starting OLED...");
  display.display();
  delay(1000);

  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
}

void loop() {
  updateScreen();
  delay(100);  // smooth rotation
}
