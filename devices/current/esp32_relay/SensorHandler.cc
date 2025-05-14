/*
  *
  * Sensor Handler for the sensors
  *
*/

#include "SensorHandler.hh"
#include <Arduino.h>

// Sensor Pinouts
#define DHTPIN 4
#define DHTTYPE DHT11
#define LIGHT_SENSOR_PIN 34
#define SOIL_MOISTURE_PIN 35

SensorHandler::SensorHandler() : dht(DHTPIN, DHTTYPE) {
  dht.begin(); 
}

float SensorHandler::getTemperature() {
  // is nan
    return dht.readTemperature();
}

float SensorHandler::getHumidity() {
  return dht.readHumidity();
}

float SensorHandler::getLightLevel() {
  // int light_level = map(raw_light, 0, 4095, 0, 100);
  return analogRead(LIGHT_SENSOR_PIN);
}

float SensorHandler::getSoilMoisture() {
  // int soil_moisture = map(raw_soil, 0, 4095, 0, 100);
  return analogRead(SOIL_MOISTURE_PIN);
}
