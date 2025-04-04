package com.asterlink.rest.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.asterlink.rest.model.SentinelDevice;
import com.asterlink.rest.repository.SentinelDeviceRepository;
import com.asterlink.rest.service.SentinelDeviceService;

/**
 * Implementation for SentinelDevice service.
 * Code for defined functions.
 * @author jrubow
 */

@Service
public class SentinelDeviceServiceImpl implements SentinelDeviceService {
    // Set up repository access.
    private final SentinelDeviceRepository sentinelDeviceRepository;
    public SentinelDeviceServiceImpl(SentinelDeviceRepository sentinelDeviceRepository) {
        this.sentinelDeviceRepository = sentinelDeviceRepository;
    }

    @Override
    public long createSentinelDevice(SentinelDevice device) {
        try {
            device.setLastOnline(LocalDateTime.now());
            sentinelDeviceRepository.save(device);
            return sentinelDeviceRepository.findMaxDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public long createSentinelDeviceBatch(List<SentinelDevice> devices) {
        for (SentinelDevice d: devices) {
            if (createSentinelDevice(d) == -1) {
                return -1;
            }
        }
        return sentinelDeviceRepository.findMaxDeviceId();
    }


    @Override
    public boolean updateSentinelDevice(Map<String, Object> updates) {
        long id = (Long) updates.get("device_id");
        System.out.println(id);
        if (id < 0) {
            throw new IllegalArgumentException("device_id is required for updating user details.");
        }

        // Find the Device by id
        SentinelDevice device = (SentinelDevice) sentinelDeviceRepository.findById(id).orElse(null);
        if (device == null) {
            return false; // Device not found
        }


        updates.remove("device_id");
        Set<String> allowedFields = Set.of("latitude", "longitude", "battery_life", "last_online", "deployed",  "deployed_date", "password", "num_connected_devices");

        for (String key : updates.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new IllegalArgumentException("Field '" + key + "' cannot be modified.");
            }
        }

        updates.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "latitude" -> device.setLatitude((Double) value);
                    case "longitude" -> device.setLongitude((Double) value);
                    case "battery_life" -> device.setBatteryLife((Integer) value);
                    case "is_connected" -> device.setIsConnected((boolean) value);
                    case "last_online" -> device.setLastOnline((LocalDateTime) value);
                    case "deployed" -> device.setDeployed((boolean) value);
                    case "deployed_date" -> device.setDeployedDate((LocalDateTime) value);
                    case "num_connected_devices" -> device.setNumConnectedDevices((Integer) value);
                    case "password" -> device.setPassword((String) value);
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            }
        });

        // Save updated device to the repository
        try {
            sentinelDeviceRepository.save(device);
            return true; // Successfully updated
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return false; // Failed to update
        }
    }

    @Override
    public String deleteSentinelDevice(long id) {
        sentinelDeviceRepository.deleteById(id);
        return "SENTINEL DEVICE DELETED FROM DATABASE";
    }

    @Override
    public SentinelDevice getSentinelDevice(long id) {
        return sentinelDeviceRepository.findById(id).orElse(null);
    }

    @Override
    public List<SentinelDevice> getAllSentinelDevices() {
        return sentinelDeviceRepository.findAll();
    }

    @Override
    public String claimSentinelDevice(long deviceId, String password, int clientId) {
        System.out.println(clientId);
        SentinelDevice device = sentinelDeviceRepository.findById(deviceId).orElse(null);
        if (device == null || device.getPassword() == null) {
            return "DEVICE DOES NOT EXIST";
        } else if (device.getClientId() != 0) {
            return "DEVICE IS ALREADY REGISTERED";
        } else if (!device.getPassword().equals(password)) {
            return "INCORRECT DEVICE PASSWORD";
        }
        device.setAgencyId(clientId);
        try {
            sentinelDeviceRepository.save(device);
            return "ok " + device.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "INTERNAL SERVER ERROR";
        }
    }

    @Override
    public List<SentinelDevice> findByClientId(int clientId) {
        return sentinelDeviceRepository.findByClientId(clientId);
    }

    // Update location reading
    @Override
    public boolean updateLocation(long deviceId, double latitude, double longitude) {
        SentinelDevice s = sentinelDeviceRepository.findById(deviceId).orElse(null);
        if (s == null) {
            return false;
        }
        sentinelDeviceRepository.updateLastOnline(deviceId, LocalDateTime.now());
        sentinelDeviceRepository.updateLocation(deviceId, latitude, longitude);
        return true;
    }

    @Override
    public boolean updateBattery(long deviceId, double battery) {
        SentinelDevice s = sentinelDeviceRepository.findById(deviceId).orElse(null);
        if (s == null) {
            return false;
        }
        sentinelDeviceRepository.updateLastOnline(deviceId, LocalDateTime.now());
        sentinelDeviceRepository.updateBattery(deviceId, battery);
        return true;
    }
}
