package com.asterlink.rest.repository;

import com.asterlink.rest.model.SensorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SensorRecord
 * @author gl3bert
 */

@Repository
public interface SensorRecordRepository extends JpaRepository<SensorRecord, Integer> {

}
