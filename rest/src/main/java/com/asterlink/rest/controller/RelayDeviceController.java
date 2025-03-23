package com.asterlink.rest.controller;

import com.asterlink.rest.model.RelayDevice;
import com.asterlink.rest.service.RelayDeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Relay Device API processing
 * @author jrubow
 */

@RestController
@RequestMapping("/api/devices/relay")
public class RelayDeviceController {
    // Set up service access.
    RelayDeviceService relayDeviceService;
    public RelayDeviceController(RelayDeviceService relayDeviceService) {
        this.relayDeviceService = relayDeviceService;
    }

    // Initialize single relay device.
    @PostMapping("/initialize")
    public ResponseEntity<String> initalizeRelayDevice(@RequestBody RelayDevice device) {
        boolean isCreated = relayDeviceService.createRelayDevice(device);
        return isCreated ? ResponseEntity.ok("RELAY DEVICE REGISTERED") : ResponseEntity.status(400).body("RELAY DEVICE ALREADY EXISTS");
    }

    // Initialize a batch of relay devices.
    @PostMapping("/initialize/batch")
    public ResponseEntity<String> initializeRelayDeviceBatch(@RequestBody List<RelayDevice> devices) {
        boolean isCreated = relayDeviceService.createRelayDeviceBatch(devices);
        return isCreated ? ResponseEntity.ok("SENTINEL DEVICES REGISTERED") : ResponseEntity.status(400).body("ERROR REGISTERING DEVICES");
    }

    // Get all relay devices.
    @GetMapping("/all")
    public ResponseEntity<List<RelayDevice>> getRelayDevices() {
        List<RelayDevice> devices = relayDeviceService.getAllRelayDevices();
        if (devices == null || devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(devices);
        }
    }

    // Get all relay devices by sentinelId.
    @GetMapping("/network/{sentinelId}")
    public ResponseEntity<List<RelayDevice>> getRelayDevicesBySentielId(@PathVariable long sentinelId) {
        List<RelayDevice> devices = relayDeviceService.getRelayDevicesBySentinelId(sentinelId);
        if (devices == null || devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(devices);
        }
    }

    // Get relay device by its deviceId.
    @GetMapping("/get/{deviceId}")
    public ResponseEntity<RelayDevice> getRelayDeviceById(@PathVariable long deviceId) {
        RelayDevice device = relayDeviceService.getRelayDevice(deviceId);
        if (device == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(device);
        }
    }
}