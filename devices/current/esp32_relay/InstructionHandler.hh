/*
  *
  * Header file for instructions.cc
  *
*/

#ifndef INSTRUCTION_HANDLER_HH
#define INSTRUCTION_HANDLER_HH

#include "Sensorhandler.hh"
#include "painlessMesh.h"
#include <ArduinoJson.h>
#include <Preferences.h>
#include <sys/time.h>
#include <string>

class InstructionHandler {
public:
    InstructionHandler(struct timeval &tvRef, uint64_t &deviceIdRef, uint64_t &sentinelIdRef, Preferences &preferencesRef, int (*activateRef)(), painlessMesh &meshRef, unsigned char &stateRef);

    const StaticJsonDocument<200> * getInstruction() const;
    int executeInstruction(const std::string &json);
    int executeSpecifiedInstruction(uint64_t instructionId);
    int setInstruction(const std::string &json);

private:
    StaticJsonDocument<200> instruction;
    struct timeval& tv;
    Preferences &preferences;
    SensorHandler sensorHandler;
    uint64_t &deviceId;
    uint64_t &sentinelId;
    int (*activate)();
    painlessMesh &mesh;
    unsigned char &state;
};

#endif