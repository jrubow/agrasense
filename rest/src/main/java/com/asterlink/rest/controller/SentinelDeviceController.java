package com.asterlink.rest.controller;

import com.asterlink.rest.model.SentinelDevice;
import com.asterlink.rest.service.SentinelDeviceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Sentinel Device API processing
 * @author jrubow
 */

@RestController
@RequestMapping("/api/devices/sentinel")
public class SentinelDeviceController {
    // Set up service access.
    SentinelDeviceService sentinelDeviceService;
    public SentinelDeviceController(SentinelDeviceService sentinelDeviceService) {
        this.sentinelDeviceService = sentinelDeviceService;
    }

    // Initialize single sentinel device.
    @PostMapping("/initialize")
    public ResponseEntity<String> initalizeSentinelDevice(@RequestBody SentinelDevice device) {
        int isCreated = sentinelDeviceService.createSentinelDevice(device);
        return isCreated != -1 ? ResponseEntity.ok("SENTINEL DEVICE REGISTERED " + isCreated) : ResponseEntity.status(400).body("SENTINEL DEVICE ALREADY EXISTS");
    }

    // Initialize a batch of sentinel devices.
    @PostMapping("/initialize/batch")
    public ResponseEntity<String> initalizeSentinelDeviceBatch(@RequestBody List<SentinelDevice> devices) {
        int isCreated = sentinelDeviceService.createSentinelDeviceBatch(devices);
        return isCreated != -1 ? ResponseEntity.ok("SENTINEL DEVICES REGISTERED; LAST ID: " + isCreated) : ResponseEntity.status(400).body("ERROR REGISTERING DEVICES");
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateSentinelDevice(@RequestBody Map<String, Object> updates) {
        try {
            boolean isUpdated = sentinelDeviceService.updateSentinelDevice(updates);
            return isUpdated ? ResponseEntity.ok("SENTINEL DEVICE UPDATED") : ResponseEntity.status(404).body("SENTINEL DEVICE NOT FOUND");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/claim")
    public ResponseEntity<String> claimSentinelDevice(
            @RequestParam("device_id") int deviceId,
            @RequestParam("password") String password,
            @RequestParam("client_id") int clientId) {
        String claimRes = "";
        try {
            claimRes = sentinelDeviceService.claimSentinelDevice(deviceId, password, clientId);
            System.out.println(claimRes);
            return claimRes.equals("ok " + deviceId) ?
                    ResponseEntity.ok("SENTINEL DEVICE " + deviceId + " CLAIMED") :
                    ResponseEntity.status(400).body(claimRes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(claimRes);
        }
    }

    @PostMapping("/get/client-id")
    public ResponseEntity<List<SentinelDevice>> getSentinelDeviceByAgencyId(@RequestBody Map<String, Object> body) {
        List<SentinelDevice> devices = sentinelDeviceService.findByClientId((Integer) body.get("client_id"));
        if (devices == null || devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(devices);
        }
    }

    /*
    @GetMapping()
    public List<SentinelDevice> getAllUserDetails(String username) {
        return sentinelDeviceService.getAllSentinelDevices();
    }
     */
}