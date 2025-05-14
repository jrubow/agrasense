// InstructionHandler.cpp

#include "InstructionHandler.hh"
#include "configuration.h"
#include "painlessMesh.h"
#include <sys/time.h>
#include <Preferences.h>
#include <WiFi.h>
#include <esp_sleep.h>

/// Sleep duration: 1 hour in microseconds
static constexpr uint64_t SLEEP_DURATION_US = 1ULL * 60ULL * 1000000ULL;

uint64_t macAddressToInteger(const String& mac);

InstructionHandler::InstructionHandler(
    struct timeval &tvRef,
    uint64_t &deviceIdRef,
    uint64_t &sentinelIdRef,
    Preferences &preferencesRef,
    int (*activateRef)(),
    painlessMesh &meshRef,
    unsigned char &stateRef)
  : tv(tvRef),
    deviceId(deviceIdRef),
    sentinelId(sentinelIdRef),
    preferences(preferencesRef),
    activate(activateRef),
    sensorHandler(),
    mesh(meshRef),
    state(stateRef)
{
  Serial.println("[DEBUG] InstructionHandler constructed");
}

int InstructionHandler::setInstruction(const std::string &json)
{
  Serial.printf("[DEBUG] setInstruction called with JSON: %s\n", json.c_str());
  DeserializationError error = deserializeJson(instruction, json);
  if (error) {
    Serial.printf("[ERROR] JSON deserialization failed: %s\n", error.c_str());
    return JSON_DESERIALIZATION_ERROR;
  }
  Serial.println("[DEBUG] JSON deserialized successfully");
  return OK;
}

int InstructionHandler::executeInstruction(const std::string &json)
{
  Serial.println("[DEBUG] executeInstruction entry");
  int error = setInstruction(json);
  if (error) {
    Serial.printf("[ERROR] setInstruction returned error code %d\n", error);
    return error;
  }

  uint64_t instrType = instruction["instruction_type"].as<uint64_t>();
  Serial.printf("[DEBUG] Instruction type: %llu\n", instrType);

  if (instrType == SYNCHRONIZE_TIME) {
    time_t receivedEpoch = instruction["epoch"].as<time_t>();
    Serial.printf("[DEBUG] Synchronizing time to epoch: %ld\n", receivedEpoch);
    tv.tv_sec  = receivedEpoch;
    tv.tv_usec = 0;
    settimeofday(&tv, nullptr);

    Serial.println("[DEBUG] Entering deep sleep mode");
    esp_sleep_enable_timer_wakeup(SLEEP_DURATION_US);
    // No explicit esp_sleep_pd_config for RTC_PERIPH: AUTO mode handles it without triggering the assert
    esp_deep_sleep_start();
  }
  else if (instrType == CONFIGURE_RELAY_DEVICE) {
    Serial.println("[DEBUG] CONFIGURE_RELAY_DEVICE received");
    StaticJsonDocument<200> jsonDoc;

    sentinelId = instruction["sentinel_id"].as<uint64_t>();
    Serial.printf("[DEBUG] Updated sentinelId: %llu\n", sentinelId);
    preferences.putULong("sentinel_id", sentinelId);

    state = ACTIVE;
    Serial.printf("[DEBUG] State set to ACTIVE (%d)\n", state);
    preferences.putUChar("state", state);

    deviceId = macAddressToInteger(WiFi.macAddress());
    Serial.printf("[DEBUG] Generated deviceId from MAC: %llu\n", deviceId);
    preferences.putULong("device_id", deviceId);

    // Prepare CONFIGURE_DEVICE_ID instruction
    jsonDoc["instruction_type"] = CONFIGURE_DEVICE_ID;
    jsonDoc["device_id"]        = deviceId;
    jsonDoc["password"]         = "password";

    String instr;
    serializeJson(jsonDoc, instr);
    Serial.printf("[DEBUG] Sending CONFIGURE_DEVICE_ID instruction: %s\n", instr.c_str());
    mesh.sendSingle(sentinelId, instr);

    Serial.println("[DEBUG] Calling activate()");
    activate();
  }
  else {
    Serial.printf("[WARN] Unknown instruction type: %llu\n", instrType);
  }

  Serial.println("[DEBUG] executeInstruction exiting");
  return OK;
}

int InstructionHandler::executeSpecifiedInstruction(uint64_t instructionId)
{
  Serial.printf("[DEBUG] executeSpecifiedInstruction called with ID: %llu\n", instructionId);
  if (instructionId == SEND_SENSOR_DATA) {
    Serial.println("[DEBUG] Preparing sensor data packet");
    StaticJsonDocument<200> jsonDoc;
    jsonDoc["device_id"]      = deviceId;
    jsonDoc["temperature"]    = sensorHandler.getTemperature();
    jsonDoc["humidity"]       = sensorHandler.getHumidity();
    jsonDoc["light_level"]    = sensorHandler.getLightLevel();
    jsonDoc["soil_moisture"]  = sensorHandler.getSoilMoisture();
    jsonDoc["instruction_type"] = SEND_SENSOR_DATA;

    String instr;
    serializeJson(jsonDoc, instr);
    Serial.printf("[DEBUG] Sending sensor data: %s\n", instr.c_str());
    mesh.sendSingle(sentinelId, instr);
  }
  else {
    Serial.printf("[WARN] executeSpecifiedInstruction received unknown ID: %llu\n", instructionId);
  }
  return OK;
}

uint64_t macAddressToInteger(const String& mac)
{
  Serial.printf("[DEBUG] macAddressToInteger called with MAC: %s\n", mac.c_str());
  String cleanedMAC = mac;
  cleanedMAC.replace(":", "");
  uint64_t result = strtoull(cleanedMAC.c_str(), nullptr, 16);
  Serial.printf("[DEBUG] Converted MAC to integer: %llu\n", result);
  return result;
}
