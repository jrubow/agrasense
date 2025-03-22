package com.asterlink.rest.controller;

import com.asterlink.rest.model.RecordAverage;
import com.asterlink.rest.model.Record;
import com.asterlink.rest.service.RecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Record class.
 * Handles API requests.
 * @author Gleb Bereziuk (gl3bert)
 */

@RestController
@RequestMapping("/api/record")
public class RecordController {

    // Set up service access.
    RecordService recordService;
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // Return by record ID.
    @GetMapping("{record_id}")
    public Record getSensorRecordDetails(@PathVariable("record_id") int recordId) {
        return recordService.getRecord(recordId);
    }

    // Get the list of all records in the table.
    @GetMapping("/all-records")
    public List<Record> getAllRecords() {
        return recordService.getAllRecords();
    }

    // Add new record.
    @PostMapping
    public String createRecord(@RequestBody Record record) {
        recordService.createRecord(record);
        return "Record created.";
    }

    // Add multiple new records.
    @PostMapping("/batch")
    public String createBatchRecord(@RequestBody List<Record> records) {
        recordService.createBatchRecord(records);
        return "Records created.";
    }

    /*
    @GetMapping("/averages/{deviceId}")
    public List<RecordAverage> getLastDeviceAverages(@PathVariable int deviceId) {
        return recordService.getLastDeviceAverages(deviceId);
    }
     */
}
