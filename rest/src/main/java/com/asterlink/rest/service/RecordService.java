package com.asterlink.rest.service;

import com.asterlink.rest.model.Record;
import com.asterlink.rest.model.RecordAverageDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Record
 * @author gl3bert
 */

public interface RecordService {
    boolean createRecord(Record record);
    public Record getRecord(int recordId);
    public List<Record> getAllRecords();
    public List<Record> getRecordsByDevice(long deviceId);
    public List<Record> getNRecordsByDevice(long deviceId, int n);
    public void createBatchRecord(List<Record> records);
    List<RecordAverageDTO> getAveragesByTypeAndInterval(int type, LocalDateTime start, LocalDateTime end, int interval);

}
