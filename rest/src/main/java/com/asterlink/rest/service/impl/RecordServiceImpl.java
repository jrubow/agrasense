package com.asterlink.rest.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.asterlink.rest.model.Record;
import com.asterlink.rest.repository.RecordRepository;
import com.asterlink.rest.service.RecordService;

/**
 * Implementation for Record service.
 * @author Josh Rubow (jrubow), Gleb Bereziuk (gl3bert)
 */

@Service
public class RecordServiceImpl implements RecordService {
    // Set up repository access.
    private final RecordRepository recordRepository;
    public RecordServiceImpl(RecordRepository r) {
        this.recordRepository = r;
    }

    // Enter record into the table.
    @Override
    public boolean createRecord(Record record) {
        try {
            record.setRecordId(getNextRecordId());
            LocalDateTime now = LocalDateTime.now();
            record.setTimestamp(now);
            recordRepository.save(record);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Enter a list of records into the table.
    @Override
    public void createBatchRecord(List<Record> records) {
        for (Record r : records) {
            createRecord(r);
        }
    }

    // Get record by its ID.
    @Override
    public Record getRecord(int recordId) {
        return recordRepository.findById(recordId).orElse(null);
    }

    // Get all records.
    @Override
    public List<Record> getAllRecords() {
        return recordRepository.findAll();
    }

    // Get records by device ID.
    @Override
    public List<Record> getRecordsByDevice(long deviceId) {
        return recordRepository.findRecordsByDeviceId(deviceId);
    }

    // Get next ID number for a new record.
    public int getNextRecordId() {
        Integer maxId = recordRepository.findMaxRecordId();
        return (maxId == null) ? 1 : maxId + 1;
    }
}
