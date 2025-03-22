package com.asterlink.rest.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Class for sentinel devices.
 * Extends parameters of Device class.
 * @author jrubow
 */

@Entity
@Table(name = "sentinel_devices")
public class SentinelDevice extends Device {

    // Additional fields specific to SentinelDevice
    @Column(name="num_connected_devices")
    private int numConnectedDevices;
    @Column(name="client_id")
    private int clientId;
    @Column(name="password")
    private String password;

    // Default constructor
    public SentinelDevice() {}

    // Parameterized constructor
    @JsonCreator
    public SentinelDevice(
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("battery_life") double batteryLife,
            @JsonProperty("last_online") LocalDateTime lastOnline,
            @JsonProperty("deployed_date") LocalDateTime deployedDate,
            @JsonProperty("deployed") boolean deployed,
            @JsonProperty("is_connected") boolean isConnected,
            @JsonProperty("num_connected_devices") int numConnectedDevices) {
        super(deviceId, latitude, longitude, batteryLife, lastOnline, deployedDate, deployed);
        this.numConnectedDevices = numConnectedDevices;
        this.password = generatePassword(8);
        this.clientId = 0;
    }

    // Password generator.
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    // Getters and Setters for additional fields
    public int getNumConnectedDevices() { return numConnectedDevices; }
    public void setNumConnectedDevices(int numConnectedDevices) { this.numConnectedDevices = numConnectedDevices; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }
    public int getClientId() { return this.clientId; }
    public void setAgencyId(int clientId) {
        System.out.println(clientId);
        this.clientId = clientId;
    }
}
