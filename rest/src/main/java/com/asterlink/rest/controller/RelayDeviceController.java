package com.asterlink.rest.controller;

import com.asterlink.rest.model.RelayDevice;
import com.asterlink.rest.service.RelayDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}