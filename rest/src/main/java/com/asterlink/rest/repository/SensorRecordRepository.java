package com.asterlink.rest.repository;

import com.asterlink.rest.model.SensorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository interface for SensorRecord
 * @author gl3bert
 */

public interface SensorRecordRepository extends JpaRepository<SensorRecord, Integer> {

    @Query("SELECT MAX(r.record_id) FROM SensorRecord r")
    Integer findMaxRecordId();

}

