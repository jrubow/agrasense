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
    unsigned char &stateRef,
    void (*logRef)(const char *)
    ) 
    : tv(tvRef),
    deviceId(deviceIdRef),
    sentinelId(sentinelIdRef),
    preferences(preferencesRef),
    activate(activateRef),
    sensorHandler(),
    mesh(meshRef),
    state(stateRef),
    log(logRef)
{
    log("InstructionHandler constructed");
    Serial.println("InstructionHandler initialized");
}

int InstructionHandler::setInstruction(const std::string &json)
{
    log("Parsing instruction JSON");
    DeserializationError error = deserializeJson(instruction, json);
    if (error) {
        log("JSON deserialization failed");
        return JSON_DESERIALIZATION_ERROR;
    }
    log("Instruction parsed successfully");
    return OK;
}

int InstructionHandler::executeInstruction(const std::string &json)
{
    log("executeInstruction called");
    int error = setInstruction(json);
    if (error) {
        log("setInstruction returned error");
        return error;
    }

    uint64_t instrType = instruction["instruction_type"].as<uint64_t>();
    {
        char buf[64];
        snprintf(buf, sizeof(buf), "Instruction type: %llu", instrType);
        log(buf);
    }

    if (instrType == SYNCHRONIZE_TIME) {
        time_t receivedEpoch = instruction["epoch"].as<time_t>();
        log("Synchronizing time");
        tv.tv_sec  = receivedEpoch;
        tv.tv_usec = 0;
        settimeofday(&tv, nullptr);

        Serial.println("Entering deep sleep mode");
        esp_sleep_enable_timer_wakeup(SLEEP_DURATION_US);
        esp_deep_sleep_start();
    }
    else if (instrType == CONFIGURE_RELAY_DEVICE) {
        log("Configuring relay device");
        sentinelId = instruction["sentinel_id"].as<uint64_t>();
        preferences.putULong("sentinel_id", sentinelId);
        {
            char buf[64];
            snprintf(buf, sizeof(buf), "Sentinel ID updated: %llu", sentinelId);
            log(buf);
        }

        state = ACTIVE;
        preferences.putUChar("state", state);
        log("State set to ACTIVE");

        deviceId = macAddressToInteger(WiFi.macAddress());
        preferences.putULong("device_id", deviceId);
        {
            char buf[64];
            snprintf(buf, sizeof(buf), "Device ID generated: %llu", deviceId);
            log(buf);
        }

        StaticJsonDocument<200> jsonDoc;
        jsonDoc["instruction_type"] = CONFIGURE_DEVICE_ID;
        jsonDoc["device_id"]        = deviceId;
        jsonDoc["password"]         = "password";

        String instr;
        serializeJson(jsonDoc, instr);
        log("Sending CONFIGURE_DEVICE_ID");
        mesh.sendSingle(sentinelId, instr);

        activate();
        log("Activation executed");
    }
    else {
        log("Unknown instruction type");
    }

    return OK;
}

int InstructionHandler::executeSpecifiedInstruction(uint64_t instructionId)
{
    if (instructionId == SEND_SENSOR_DATA) {
        log("Sending sensor data");
        StaticJsonDocument<200> jsonDoc;
        jsonDoc["device_id"]       = deviceId;
        jsonDoc["temperature"]     = sensorHandler.getTemperature();
        jsonDoc["humidity"]        = sensorHandler.getHumidity();
        jsonDoc["light_level"]     = sensorHandler.getLightLevel();
        jsonDoc["soil_moisture"]   = sensorHandler.getSoilMoisture();
        jsonDoc["instruction_type"] = SEND_SENSOR_DATA;

        String instr;
        serializeJson(jsonDoc, instr);
        log("Sensor data serialized");
        mesh.sendSingle(sentinelId, instr);
    }
    else {
        log("executeSpecifiedInstruction: unknown ID");
    }
    return OK;
}

uint64_t macAddressToInteger(const String& mac)
{
    String cleanedMAC = mac;
    cleanedMAC.replace(":", "");
    uint64_t result = strtoull(cleanedMAC.c_str(), nullptr, 16);
    return result;
}
