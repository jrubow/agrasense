package com.asterlink.rest.repository;

import com.asterlink.rest.model.SensorRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for SensorRecord
 * @author gl3bert
 */

public interface SensorRecordRepository extends JpaRepository<SensorRecord, Integer> {

}
