/*
  *
  * Configuration Ids and States
  *
*/

// Guard rail to Prevent multiple includes
#ifndef CONFIGURATION_H
#define CONFIGURATION_H

// Device States
#define INIT 0
#define ACTIVE 1

// Sensor Ids
#define TEMPERATURE 1
#define HUMIDITY 2

// Instruction Ids
#define CONFIGURE_RELAY_DEVICE 1
#define CONFIGURE_DEVICE_ID 2
#define SEND_SENSOR_DATA 3
#define SYNCHRONIZE_TIME 4

// Status Signals
#define OK 0
#define JSON_DESERIALIZATION_ERROR 1

#endif