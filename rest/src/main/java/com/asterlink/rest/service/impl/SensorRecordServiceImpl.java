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
        List<SensorRecord> records = getAllSensorRecords();

        if (records.isEmpty()) {
            return averageResponses;
        }

        ZonedDateTime now = ZonedDateTime.now();

        List<SensorRecord> filteredRecords = records.stream()
                .filter(record -> {
                    ZonedDateTime timestamp = ZonedDateTime.parse(record.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)
                            .withZoneSameInstant(now.getZone());
                    return ChronoUnit.MINUTES.between(timestamp, now) <= 60;
                })
                .collect(Collectors.toList());

        if (filteredRecords.isEmpty()) {
            return averageResponses;
        }

        filteredRecords.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));

        Map<String, List<SensorRecord>> groupedByInterval = filteredRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    ZonedDateTime timestamp = ZonedDateTime.parse(record.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME)
                            .withZoneSameInstant(now.getZone());
                    int intervalStart = (timestamp.getMinute() / 10) * 10;
                    ZonedDateTime intervalStartTime = timestamp.withMinute(intervalStart).withSecond(0).withNano(0);
                    return intervalStartTime.format(DateTimeFormatter.ISO_DATE_TIME);
                }));

        List<Map.Entry<String, List<SensorRecord>>> sortedIntervals = new ArrayList<>(groupedByInterval.entrySet());
        sortedIntervals.sort((a, b) -> b.getKey().compareTo(a.getKey()));

        List<Map.Entry<String, List<SensorRecord>>> lastSixIntervals = sortedIntervals.stream()
                .limit(6)
                .toList();

        for (Map.Entry<String, List<SensorRecord>> entry : lastSixIntervals) {
            double avgTemp = entry.getValue().stream().mapToDouble(SensorRecord::getTemp).average().orElse(0);
            double avgHumidity = entry.getValue().stream().mapToInt(SensorRecord::getHumidity).average().orElse(0);
            double avgLight = entry.getValue().stream().mapToInt(SensorRecord::getLight).average().orElse(0);
            double avgSoil = entry.getValue().stream().mapToInt(SensorRecord::getSoil).average().orElse(0);

            averageResponses.add(new SensorAveragesResponse(
                    entry.getKey(),
                    avgTemp,
                    avgHumidity,
                    avgLight,
                    avgSoil
            ));
        }
        return averageResponses;
    }
}
