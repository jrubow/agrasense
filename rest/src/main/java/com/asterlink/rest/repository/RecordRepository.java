package com.asterlink.rest.repository;

import com.asterlink.rest.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Record.
 * Accesses data from the MySQL table.
 * @author gl3bert
 */

public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Query("SELECT MAX(r.recordId) FROM Record r")
    Integer findMaxRecordId();

    @Query("SELECT r FROM Record r WHERE r.deviceId = :deviceId")
    List<Record> findRecordsByDeviceId(long deviceId);

    @Query(value = "SELECT * FROM records r WHERE r.device_id = ?1 ORDER BY r.timestamp DESC LIMIT ?2", nativeQuery = true)
    List<Record> findLastNRecordsByDeviceId(@Param("deviceId") long deviceId, @Param("n") int n);

    @Query(value = """
        SELECT 
            FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(r.timestamp) / (:interval * 60)) * (:interval * 60)) AS time_group,
            AVG(r.value) AS avg_value
        FROM records r
        WHERE r.type = :type 
          AND r.timestamp BETWEEN :start AND :end
        GROUP BY time_group
        ORDER BY time_group
    """, nativeQuery = true)
    List<Object[]> findAveragesByTypeAndInterval(
            @Param("type") int type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("interval") int interval
    );
}
