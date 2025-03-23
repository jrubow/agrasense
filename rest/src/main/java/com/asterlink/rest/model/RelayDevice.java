package com.asterlink.rest.model;

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

    @Column(name = "sentinel_id")
    @JsonProperty("sentinel_id")
    private long sentinelId;

    @Column(name = "sentinel_connection")
    @JsonProperty("sentinel_connection")
    private boolean sentinelConnection;

    @Column(name = "password")
    @JsonProperty("password")
    private String password;

    @Column(name = "client_id")
    @JsonProperty("client_id")
    private int clientId;

    // Default Constructor
    public RelayDevice() {};

    // Main Constructor
    @JsonCreator
    public RelayDevice(
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("sentinel_id") long sentinelId,
            @JsonProperty("password") String password) {
        super(deviceId);
        this.sentinelId = sentinelId;
        this.sentinelConnection = false;
        this.password = password;
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
    public long getSentinelId() { return this.sentinelId; }
    public void setSentinelId(long sentinelId) { this.sentinelId = sentinelId; }
    public boolean getSentinelConnection() { return this.sentinelConnection; }
    public void setSentinelConnection(boolean sentinelConnection) { this.sentinelConnection = sentinelConnection; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }
    public int getClientId() { return this.clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
}
