package com.asterlink.rest.controller;

import com.asterlink.rest.model.SensorAveragesResponse;
import com.asterlink.rest.model.SensorRecord;
import com.asterlink.rest.service.SensorRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SensorRecords RESTful API logic
 * @author gl3bert
 */

@RestController
@RequestMapping("/record")
public class SensorRecordController {

    SensorRecordService sensorRecordService;
    public SensorRecordController(SensorRecordService sensorRecordService) {
        this.sensorRecordService = sensorRecordService;
    }

    @GetMapping("{record_id}")
    public SensorRecord getSensorRecordDetails(@PathVariable("record_id") int record_id) {
        return sensorRecordService.getSensorRecord(record_id);
    }

    @GetMapping("/all-records")
    public List<SensorRecord> getAllSensorRecords() {
        return sensorRecordService.getAllSensorRecords();
    }

    @PostMapping
    public String createSensorRecordDetails(@RequestBody SensorRecord sensorRecord) {
        sensorRecordService.createSensorRecord(sensorRecord);
        return "Record created.";
    }

    @PostMapping("/batch")
    public String createMultipleSensorRecords(@RequestBody List<SensorRecord> sensorRecords) {
        sensorRecordService.createMultipleSensorRecords(sensorRecords);
        return "Records created.";
    }

    @PutMapping
    public String updateSensorRecordDetails(@RequestBody SensorRecord sensorRecord) {
        sensorRecordService.updateSensorRecord(sensorRecord);
        return "Record updated.";
    }

    @DeleteMapping("{record_id}")
    public String deleteSensorRecordDetails(@PathVariable int record_id) {
        sensorRecordService.deleteSensorRecord(record_id);
        return "Record deleted.";
    }

    @GetMapping("/averages")
    public SensorAveragesResponse getSensorAveragesRecord() {
        return sensorRecordService.getSensorAveragesRecord();
    }
}
