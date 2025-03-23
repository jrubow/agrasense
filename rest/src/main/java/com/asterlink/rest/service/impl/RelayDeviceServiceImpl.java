package com.asterlink.rest.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.asterlink.rest.model.RelayDevice;
import com.asterlink.rest.repository.RelayDeviceRepository;
import com.asterlink.rest.service.RelayDeviceService;

/**
 * Implementation for RelayDevice service.
 * Code for defined functions.
 * @author jrubow
 */


@Service
public class RelayDeviceServiceImpl implements RelayDeviceService {
    private final RelayDeviceRepository relayDeviceRepository;

    // Constructor for RelayDeviceServiceImpl
    public RelayDeviceServiceImpl(RelayDeviceRepository relayDeviceRepository) {
        this.relayDeviceRepository = relayDeviceRepository;
    }

    @Override
    public boolean createRelayDevice(RelayDevice device) {
        try {
            relayDeviceRepository.save(device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createRelayDeviceBatch(List<RelayDevice> devices) {
        for (RelayDevice d: devices) {
            if (!createRelayDevice(d)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String updateRelayDevice(Map<String, Object> updates) {
        long id = (Long) updates.get("device_id");
        if (id < 0) {
            throw new IllegalArgumentException("device_id is required for updating user details.");
        }

        // Find the Devic by id
        RelayDevice device = (RelayDevice) relayDeviceRepository.findById(id).orElse(null);
        if (device == null) {
            return "DEVICE_ID: " + id + " IS NOT FOUND";
        }

        Set<String> allowedFields = Set.of("latitude", "longitude", "battery_life", "is_connected", "last_online", "deployed", "deployed_date", "sentinel_connection");

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
                    case "sentinel_connection" -> device.setSentinelConnection((boolean) value);
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            }
        });

        return "RELAY DEVICE UPDATED";
    }

    @Override
    public String deleteRelayDevice(long id) {
        relayDeviceRepository.deleteById(id);
        return "RELAY DEVICE DELETED FROM DATABASE";
    }

    @Override
    public RelayDevice getRelayDevice(long id) {
        return relayDeviceRepository.findById(id).orElse(null);
    }

    @Override
    public List<RelayDevice> getAllRelayDevices() {
        return relayDeviceRepository.findAll();
    }

    @Override
    public List<RelayDevice> getRelayDevicesBySentinelId(long sentinelId) {
        return relayDeviceRepository.findBySentinelId(sentinelId);
    }

    @Override
    public boolean registerRelayDevice(RelayDevice device) {
        return false;
    }
}
