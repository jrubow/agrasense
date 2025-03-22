package com.asterlink.rest.service;

import com.asterlink.rest.model.Record;
import com.asterlink.rest.model.RecordAverage;

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
    public void createBatchRecord(List<Record> records);
}
