package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.SensorRecord;
import com.asterlink.rest.repository.SensorRecordRepository;
import com.asterlink.rest.service.SensorRecordService;

import java.util.List;

/**
 * Implementation for SensorRecord services
 * @author gl3bert
 */
public class SensorRecordServiceImpl implements SensorRecordService {

    SensorRecordRepository sensorRecordRepository;
    public SensorRecordServiceImpl(SensorRecordRepository sensorRecordRepository) {
        this.sensorRecordRepository = sensorRecordRepository;
    }

    @Override
    public String createSensorRecord(SensorRecord sensorRecord) {
        sensorRecordRepository.save(sensorRecord);
        return "Record added to database.";
    }

    @Override
    public String updateSensorRecord(SensorRecord sensorRecord) {
        sensorRecordRepository.save(sensorRecord);
        return "Record modified in database.";
    }

    @Override
    public String deleteSensorRecord(int recordId) {
        sensorRecordRepository.deleteById(recordId);
        return "Record deleted from database.";
    }

    @Override
    public SensorRecord getSensorRecord(int recordId) {
        return sensorRecordRepository.findById(recordId).get();
    }

    @Override
    public List<SensorRecord> getAllSensorRecords() {
        return sensorRecordRepository.findAll();
    }
}
