package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.SensorAveragesResponse;
import com.asterlink.rest.model.SensorRecord;
import com.asterlink.rest.repository.SensorRecordRepository;
import com.asterlink.rest.service.SensorRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation for SensorRecord services
 * @author gl3bert
 */

@Service
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

    @Override
    public String createMultipleSensorRecords(List<SensorRecord> sensorRecords)  {
        for (SensorRecord record : sensorRecords) {
            createSensorRecord(record);
        }
        return "Records created.";
    }

    @Override
    public SensorAveragesResponse getSensorAveragesRecord() {

        List<SensorRecord> records = getAllSensorRecords();
        if (records.isEmpty()) return null;

        int endIdx = records.size() - 1;
        int startIdx = Math.max(0, endIdx - 9);
        List<SensorRecord> lastRecords = records.subList(startIdx, endIdx + 1);

        double avgTemp = lastRecords.stream().mapToDouble(SensorRecord::getTemp).average().orElse(0);
        double avgHumidity = lastRecords.stream().mapToInt(SensorRecord::getHumidity).average().orElse(0);
        double avgLight = lastRecords.stream().mapToInt(SensorRecord::getLight).average().orElse(0);
        double avgSoil = lastRecords.stream().mapToInt(SensorRecord::getSoil).average().orElse(0);

        return new SensorAveragesResponse(
                records.get(endIdx).getTimestamp(),
                avgTemp,
                avgHumidity,
                avgLight,
                avgSoil
        );
    }
}
