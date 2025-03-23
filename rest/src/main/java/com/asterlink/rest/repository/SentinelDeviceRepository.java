package com.asterlink.rest.repository;

import com.asterlink.rest.model.SentinelDevice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for Sentinel repository
 * @author jrubow
 */

public interface SentinelDeviceRepository extends JpaRepository<SentinelDevice, Long> {

    List<SentinelDevice> findByClientId(int clientId);

    @Query("SELECT MAX(r.deviceId) FROM SentinelDevice r")
    Long findMaxDeviceId();

    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice s SET s.lastOnline = :currentDateTime WHERE s.deviceId = :deviceId")
    void updateLastOnline(@Param("deviceId") Long deviceId, @Param("currentDateTime") LocalDateTime currentDateTime);

}