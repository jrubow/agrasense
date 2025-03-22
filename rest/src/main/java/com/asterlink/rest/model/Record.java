package com.asterlink.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Records class.
 * Object used for storing device data.
 * @author Gleb Bereziuk (gl3bert)
 */

@Entity
@Table(name = "records")
public class Record {

    @Id
    @Column(name="record_id")
    private int recordId;
    @Column(name="device_id")
    private long deviceId;
    @Column(name="timestamp")
    private LocalDateTime timestamp;
    @Column(name="type")
    private int type;
    @Column(name="value")
    private float value;

    // Default empty constructor.
    public Record() {}

    // JSON parsing.
    @JsonCreator
    public Record(
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("type") int type,
            @JsonProperty("value") float value) {
        this.deviceId = deviceId;
        this.type = type;
        this.value = value;
    }

    /*
    // JSON parsing.
    @JsonCreator
    public Record(
            @JsonProperty("device_id") int deviceId,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("type") int type,
            @JsonProperty("value") float value) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
    }
    */

    // Getters and Setters.
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public long getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }

}