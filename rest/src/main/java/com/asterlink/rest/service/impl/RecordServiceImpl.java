package com.asterlink.rest.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.asterlink.rest.model.RecordAverageDTO;
import com.asterlink.rest.repository.RelayDeviceRepository;
import com.asterlink.rest.repository.SentinelDeviceRepository;
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
    private final RelayDeviceRepository relayDeviceRepository;
    private final SentinelDeviceRepository sentinelDeviceRepository;

    public RecordServiceImpl(RecordRepository r, RelayDeviceRepository rd, SentinelDeviceRepository sentinelDeviceRepository) {
        this.recordRepository = r;
        this.relayDeviceRepository = rd;
        this.sentinelDeviceRepository = sentinelDeviceRepository;
    }

    // Enter record into the table.
    @Override
    public boolean createRecord(Record record) {
        try {
            record.setRecordId(getNextRecordId());
            LocalDateTime now = LocalDateTime.now();
            record.setTimestamp(now);
            recordRepository.save(record);
            relayDeviceRepository.updateLastOnline(record.getDeviceId(), now);
            sentinelDeviceRepository.updateLastOnline(relayDeviceRepository.findSentinelIdByDeviceId(record.getDeviceId()), now);
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

    // Get records averages by type, timeframe, and intervals.
    @Override
    public List<RecordAverageDTO> getAveragesByTypeAndInterval(int type, LocalDateTime start, LocalDateTime end, int interval) {
        return recordRepository.findAveragesByTypeAndInterval(type, start, end, interval)
                .stream()
                .map(RecordAverageDTO::from)
                .toList();
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

    // Get last n records for a device.
    @Override
    public List<Record> getNRecordsByDevice(long deviceId, int n) {
        return recordRepository.findLastNRecordsByDeviceId(deviceId, n);
    }

    // Get next ID number for a new record.
    public int getNextRecordId() {
        Integer maxId = recordRepository.findMaxRecordId();
        return (maxId == null) ? 1 : maxId + 1;
    }
}
