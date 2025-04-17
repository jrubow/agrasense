/*
  *
  * Instruction Handler for Relay Devices
  *
*/

#include "InstructionHandler.hh"
#include "configuration.h"
#include "painlessMesh.h"
#include <sys/time.h>
#include <Preferences.h>

uint64_t sleepDuration = 30 * 60 * 1000000;

// Function prototypes
uint64_t macAddressToInteger(const String& mac);

// Instruction Handler Constructor
InstructionHandler::InstructionHandler( struct timeval &tvRef,
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
                                        state(stateRef) {
  // Constructor
}

// Sets instruction with error handling
int InstructionHandler::setInstruction(const std::string &json) {
  DeserializationError error = deserializeJson(instruction, json);
  if (error) {
    return JSON_DESERIALIZATION_ERROR;
  }

  return OK;
}

// Executes given instruction
int InstructionHandler::executeInstruction(const std::string &json) {
  int error = setInstruction(json);
  if (error) {
    return error;
  }

  uint64_t instrType = instruction["instruction_type"].as<uint64_t>();

  if (instrType == SYNCHRONIZE_TIME) {
    // Receive and synchronize time
    time_t receivedEpoch = instruction["epoch"].as<time_t>();
    tv.tv_sec = receivedEpoch;
    tv.tv_usec = 0;
    settimeofday(&tv, nullptr);

    Serial.printf("Sleeping for %d\n", sleepDuration);

    esp_deep_sleep(sleepDuration);
  }
  else if (instrType == CONFIGURE_RELAY_DEVICE) {
    StaticJsonDocument<200> jsonDoc;

    sentinelId = instruction["sentinel_id"].as<uint64_t>();
    preferences.putUInt("sentinel_id", sentinelId);
    
    // Update state and device Id
    state = ACTIVE;
    preferences.putUChar("state", ACTIVE);
    deviceId = macAddressToInteger(WiFi.macAddress());
    preferences.putUInt("device_id", deviceId);

    // Send CONFIGURE_DEVICE_ID instruction to sentinel device
    jsonDoc["instruction_type"] = CONFIGURE_DEVICE_ID;
    jsonDoc["device_id"] = deviceId;
    jsonDoc["password"] = "password";
    String instr;
    serializeJson(jsonDoc, instr);
    mesh.sendSingle(sentinelId, instr);

    activate();
  }

  return OK;
}

int InstructionHandler::executeSpecifiedInstruction(uint64_t instructionId) {
  if (instructionId == SEND_SENSOR_DATA) {
    // Send Data back to Sentinel
    StaticJsonDocument<200> jsonDoc;
    jsonDoc["device_id"] = deviceId;
    jsonDoc["temperature"] = sensorHandler.getTemperature();
    jsonDoc["humidity"] = sensorHandler.getHumidity();
    jsonDoc["light_level"] = sensorHandler.getLightLevel();
    jsonDoc["soil_moisture"] = sensorHandler.getSoilMoisture();
    jsonDoc["instruction_type"] = SEND_SENSOR_DATA;

    String instr;
    serializeJson(jsonDoc, instr);
    mesh.sendSingle(sentinelId, instr);
  }

  return OK;
}

uint64_t macAddressToInteger(const String& mac) {
  String cleanedMAC = mac;
  cleanedMAC.replace(":", "");
  return strtoull(cleanedMAC.c_str(), NULL, 16);
}
