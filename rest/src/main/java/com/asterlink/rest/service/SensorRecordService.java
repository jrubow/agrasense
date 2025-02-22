package com.asterlink.rest.service;

import com.asterlink.rest.model.SensorRecord;

import java.util.List;

/**
 * Service interface for Record
 * @author gl3bert
 */

public interface SensorRecordService {
    public String createSensorRecord(SensorRecord sensorRecord);
    public String updateSensorRecord(SensorRecord sensorRecord);
    public String deleteSensorRecord(int recordId);
    public SensorRecord getSensorRecord(int recordId);
    public List<SensorRecord> getAllSensorRecords();
}
