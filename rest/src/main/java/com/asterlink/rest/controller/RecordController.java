package com.asterlink.rest.controller;

import com.asterlink.rest.model.Record;
import com.asterlink.rest.model.RecordAverageDTO;
import com.asterlink.rest.service.RecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    // Get last n records from the table for a specific devices
    @GetMapping("/recent")
    public List<Record> getDeviceLastNRecords(@RequestParam("device_id") long deviceId,
                                              @RequestParam("n") int n,
                                              @RequestParam("type") int type) {
        return recordService.getNRecordsByDevice(deviceId, n, type);
    }

    // Get all records from the table for a specific device
    @GetMapping("/{device_id}")
    public List<Record> getAllByDevice(@PathVariable("device_id") long deviceId) {
        return recordService.getRecordsByDevice(deviceId);
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

    // Get the averages for a specific record type for a specific timeframe.
    @GetMapping("/average")
    public List<RecordAverageDTO> getAverages(
            @RequestParam int type,
            @RequestParam("start_timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end_timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam int interval
    ) {
        return recordService.getAveragesByTypeAndInterval(type, start, end, interval);
    }

}
