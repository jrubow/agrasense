package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.SensorAveragesResponse;
import com.asterlink.rest.model.SensorRecord;
import com.asterlink.rest.repository.SensorRecordRepository;
import com.asterlink.rest.service.SensorRecordService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation for SensorRecord services
 * @author gl3bert
 */

@Service
public class SensorRecordServiceImpl implements SensorRecordService {

    private final SensorRecordRepository sensorRecordRepository;
    public SensorRecordServiceImpl(SensorRecordRepository sensorRecordRepository) {
        this.sensorRecordRepository = sensorRecordRepository;
    }

    public int getNextRecordId() {
        Integer maxId = sensorRecordRepository.findMaxRecordId();
        return (maxId == null) ? 1 : maxId + 1;
    }

    public String createSensorRecord(SensorRecord sensorRecord) {
        sensorRecord.setRecord_id(getNextRecordId());
        sensorRecordRepository.save(sensorRecord);
        return "Record added to database.";
    }



    //@Override
    //public String createSensorRecord(SensorRecord sensorRecord) {
        //sensorRecordRepository.save(sensorRecord);
        //return "Record added to database.";

    //}

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
    public List<SensorAveragesResponse> getSensorAveragesRecord() {
        List<SensorAveragesResponse> averageResponses = new ArrayList<>();

        List<Object[]> results = sensorRecordRepository.getSensorAverages();

        for (Object[] row : results) {
            averageResponses.add(new SensorAveragesResponse(
                    row[0].toString(),  // Interval start time
                    ((Number) row[1]).doubleValue(), // avgTemp
                    ((Number) row[2]).doubleValue(), // avgHumidity
                    ((Number) row[3]).doubleValue(), // avgLight
                    ((Number) row[4]).doubleValue()  // avgSoil
            ));
        }
        return averageResponses;
    }
}
