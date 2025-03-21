package com.asterlink.rest.repository;

import com.asterlink.rest.model.Record;
import com.asterlink.rest.model.RecordAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Record> findRecordsByDeviceId(int deviceId);
}
