package com.asterlink.rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * SensorRecord object for storing one device record
 * @author gl3bert
 */

@Entity
@Table(name = "records")
public class SensorRecord {
    @Id
    private int record_id;
    private long device_id;
    private String timestamp;
    private double temp;
    private int humidity;
    private int light;
    private int soil;

    public SensorRecord() {}

    public SensorRecord(int record_id, long device_id, String timestamp, double temp, int humidity, int light, int soil) {
        this.record_id = record_id;
        this.device_id = device_id;
        this.timestamp = timestamp;
        this.temp = temp;
        this.humidity = humidity;
        this.light = light;
        this.soil = soil;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getSoil() {
        return soil;
    }

    public void setSoil(int soil) {
        this.soil = soil;
    }
}
