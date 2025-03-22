package com.asterlink.rest.model;

import java.time.LocalDateTime;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

/**
 * Abstract class for devices.
 * Outlines shared properties.
 * @author jrubow
 */

@MappedSuperclass
public abstract class Device {

    @Id
    @Column(name="device_id")
    private int deviceId;
    @Column(name="latitude")
    private double latitude;
    @Column(name="longitude")
    private double longitude;
    @Column(name="battery_life")
    private double batteryLife;
    @Column(name="last_online")
    private LocalDateTime lastOnline;
    @Column(name="deployed")
    private boolean deployed;
    @Column(name="deployed_date")
    private LocalDateTime deployedDate;
    @Column(name="is_connected")
    private boolean isConnected;

    // Default constructor
    public Device() {}

    // Parameterized constructor
    public Device(int deviceId, double latitude, double longitude, double batteryLife, LocalDateTime lastOnline,
                    LocalDateTime deployedDate, boolean deployed) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.batteryLife = batteryLife;
        this.lastOnline = lastOnline;
        this.deployed = deployed;
        this.deployedDate = deployedDate;
    }

    // Getters and Setters
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getBatteryLife() { return batteryLife; }
    public void setBatteryLife(double batteryLife) { this.batteryLife = batteryLife; }
    public LocalDateTime getLastOnline() { return lastOnline; }
    public void setLastOnline(LocalDateTime lastOnline) { this.lastOnline = lastOnline; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isDeployed() { return deployed; }
    public void setDeployed(boolean deployed) { this.deployed = deployed; }
    public LocalDateTime getDeployedDate() { return deployedDate; }
    public void setDeployedDate(LocalDateTime deployedDate) { this.deployedDate = deployedDate; }
    public boolean getIsConnected() { return isConnected; }
    public void setIsConnected(boolean isConnected) { this.isConnected = isConnected; }
}
