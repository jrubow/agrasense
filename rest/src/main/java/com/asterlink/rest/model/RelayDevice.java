package com.asterlink.rest.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Class for Relay Devices
 * @author Josh Rubow (jrubow)
 */

@Entity
@Table(name = "relay_devices")
public class RelayDevice extends Device {
    @Column(name="sentinel_id")
    private int sentinelId;
    @Column(name="sentinel_connection")
    private boolean sentinelConnection;

    // Default Constructor
    public RelayDevice() {};

    // Main Constructor
    @JsonCreator
    public RelayDevice(
            @JsonProperty("device_id") int deviceId,
            @JsonProperty("sentinel_id") int sentinelId) {
        super(deviceId);
        this.sentinelId = sentinelId;
        this.sentinelConnection = false;
    }

    /*
    // Main Constructor
    @JsonCreator
    public RelayDevice(
            @JsonProperty("device_id") int deviceId,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("battery_life") double batteryLife,
            @JsonProperty("last_online") LocalDateTime lastOnline,
            @JsonProperty("deployed_date") LocalDateTime deployedDate,
            @JsonProperty("deployed") boolean deployed,
            @JsonProperty("is_connected") boolean isConnected,
            @JsonProperty("sentinel_id") int sentinelId,
            @JsonProperty("sentinel_connection") boolean sentinelConnection) {
        super(deviceId, latitude, longitude, batteryLife, lastOnline, deployedDate, deployed);
        this.sentinelId = sentinelId;
        this.sentinelConnection = sentinelConnection;
    }
     */

    // Getters and Setters.
    public int getSentinelId() { return this.sentinelId; }
    public void setSentinelId(int sentinelId) { this.sentinelId = sentinelId; }
    public boolean getSentinelConnection() { return this.sentinelConnection; }
    public void setSentinelConnection(boolean sentinelConnection) { this.sentinelConnection = sentinelConnection; }
}
