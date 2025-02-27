package com.asterlink.rest.repository;

import com.asterlink.rest.model.SensorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Repository interface for SensorRecord
 * @author gl3bert
 */

public interface SensorRecordRepository extends JpaRepository<SensorRecord, Integer> {

    @Query("SELECT MAX(r.record_id) FROM SensorRecord r")
    Integer findMaxRecordId();

    @Query(value = """
        /* SELECT
                                                        DATE_FORMAT(
                                                            DATE_ADD(
                                                                STR_TO_DATE(SUBSTRING(timestamp, 1, 19), '%Y-%m-%dT%H:%i:%s'),
                                                                INTERVAL -(MOD(MINUTE(STR_TO_DATE(SUBSTRING(timestamp, 1, 19), '%Y-%m-%dT%H:%i:%s')), 2)) MINUTE
                                                            ),
                                                            '%Y-%m-%d %H:%i:00'
                                                        ) AS interval_end_time,
                                                        AVG(temp) AS avg_temp,
                                                        AVG(humidity) AS avg_humidity,
                                                        AVG(light) AS avg_light,
                                                        AVG(soil) AS avg_soil
                                                    FROM records
                                                    WHERE STR_TO_DATE(SUBSTRING(timestamp, 1, 19), '%Y-%m-%dT%H:%i:%s') >= UTC_TIMESTAMP() - INTERVAL 60 MINUTE
                                                    GROUP BY interval_end_time
                                                    ORDER BY interval_end_time DESC
                                                    LIMIT 6;
                                                 */
        SELECT
                    DATE_FORMAT(
                        DATE_ADD(
                            STR_TO_DATE(SUBSTRING(timestamp, 1, 19), '%Y-%m-%dT%H:%i:%s'),
                            INTERVAL -(MOD(MINUTE(STR_TO_DATE(SUBSTRING(timestamp, 1, 19), '%Y-%m-%dT%H:%i:%s')), 2)) MINUTE
                        ),
                        '%Y-%m-%d %H:%i:00'
                    ) AS interval_end_time,
                    AVG(temp) AS avg_temp,
                    AVG(humidity) AS avg_humidity,
                    AVG(light) AS avg_light,
                    AVG(soil) AS avg_soil
                FROM records
                GROUP BY interval_end_time
                ORDER BY interval_end_time DESC
                LIMIT 6;  
    """, nativeQuery = true)
    List<Object[]> getSensorAverages();

}
