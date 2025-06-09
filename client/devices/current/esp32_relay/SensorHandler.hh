/*
  *
  * Header file for sensorHandler.cc
  *
*/

#ifndef SENSOR_HANDLER_HH
#define SENSOR_HANDLER_HH

#include <DHT.h>

class SensorHandler {
public:
    SensorHandler();

    float getTemperature();
    float getHumidity();
    float getLightLevel();
    float getSoilMoisture();
    

private:
    DHT dht;
};

#endif
