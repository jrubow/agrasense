#include <Arduino.h>
#include <U8g2lib.h>
#include <Wire.h>
#include <time.h>
#include <ArduinoJson.h>

// OLED Display (I2C SSD1309 - 128x64)
U8G2_SSD1309_128X64_NONAME0_F_HW_I2C u8g2(U8G2_R0);

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
  u8g2.clearBuffer();
  u8g2.setFont(u8g2_font_6x10_tr);
  u8g2.drawStr(0, 10, "AsterLink Sentinel");
  u8g2.drawStr(0, 25, wifiConnected ? "Wi-Fi: Connected" : "Wi-Fi: Disconnected");

  char buf[32];
  snprintf(buf, sizeof(buf), "Nodes: %d", meshNodeCount);
  u8g2.drawStr(0, 40, buf);

  snprintf(buf, sizeof(buf), "Last RX: %s", lastReceivedTimestamp.c_str());
  u8g2.drawStr(0, 55, buf);

  u8g2.sendBuffer();
}

void drawNodeDataScreen() {
  u8g2.clearBuffer();
  u8g2.setFont(u8g2_font_6x10_tr);

  char buf[32];
  snprintf(buf, sizeof(buf), "Node: %s", lastNodeId.c_str());
  u8g2.drawStr(0, 10, buf);

  snprintf(buf, sizeof(buf), "Temp: %.1f C", lastTemp);
  u8g2.drawStr(0, 25, buf);

  snprintf(buf, sizeof(buf), "Humidity: %.1f%%", lastHumidity);
  u8g2.drawStr(0, 40, buf);

  snprintf(buf, sizeof(buf), "Time: %s", lastNodeTime.c_str());
  u8g2.drawStr(0, 55, buf);

  u8g2.sendBuffer();
}

void drawAlertScreen() {
  u8g2.clearBuffer();
  u8g2.setFont(u8g2_font_6x10_tr);

  u8g2.drawStr(0, 10, "! ALERTS");
  if (!wifiConnected) {
    u8g2.drawStr(0, 30, "Wi-Fi Disconnected!");
  } else {
    u8g2.drawStr(0, 30, "All systems OK.");
  }

  u8g2.sendBuffer();
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
  if (deserializeJson(doc, msg)) return;

  lastNodeId = String((uint32_t)doc["device_id"], HEX);
  lastTemp = doc["temperature"];
  lastHumidity = doc["humidity"];
  lastNodeTime = getCurrentTimeString();
  lastReceivedTimestamp = lastNodeTime;
}

void setup() {
  u8g2.begin();
  u8g2.clear();
  u8g2.setFont(u8g2_font_6x10_tr);
  u8g2.drawStr(0, 30, "Starting OLED...");
  u8g2.sendBuffer();
  delay(1000);

  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
}

void loop() {
  updateScreen();
  delay(100);  // smooth rotation
}
