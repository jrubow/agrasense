package com.asterlink.rest.model;

import java.time.LocalDateTime;

/**
 * RecordAverage class.
 * For reporting average data to the frontend tables.
 * @author Gleb Bereziuk (gl3bert)
 */

public class RecordAverage {
    private int deviceId;
    private LocalDateTime timestamp;
    private int type;
    private float value;

    // Default empty constructor.
    public RecordAverage() {}

    // Default constructor.
    public RecordAverage(int deviceId, LocalDateTime timestamp, int type, float value) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
    }

    // Getters and Setters.
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }

}
