package com.asterlink.rest.model;

import java.time.LocalDateTime;

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
    public RelayDevice(int deviceId, double latitude, double longitude, double batteryLife,
                       LocalDateTime lastOnline, LocalDateTime deployedDate, boolean deployed,
                       boolean isConnected, int sentinelId, boolean sentinelConnection) {
        super(deviceId, latitude, longitude, batteryLife, lastOnline, deployedDate, deployed);
        this.sentinelId = sentinelId;
        this.sentinelConnection = sentinelConnection;
    }

    // Getters and Setters.
    public int getSentinelId() { return this.sentinelId; }
    public void setSentinelId(int sentinelId) { this.sentinelId = sentinelId; }
    public boolean getSentinelConnection() { return this.sentinelConnection; }
    public void setSentinelConnection(boolean sentinelConnection) { this.sentinelConnection = sentinelConnection; }
}
