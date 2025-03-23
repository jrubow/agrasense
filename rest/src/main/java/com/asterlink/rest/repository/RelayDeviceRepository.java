package com.asterlink.rest.repository;

import com.asterlink.rest.model.RelayDevice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for Relay repository
 * @author jrubow
 */

public interface RelayDeviceRepository extends JpaRepository<RelayDevice, Long> {

    @Query("SELECT r FROM RelayDevice r WHERE r.sentinelId = :sentinelId")
    List<RelayDevice> findBySentinelId(long sentinelId);

    @Modifying
    @Transactional
    @Query("UPDATE RelayDevice r SET r.lastOnline = :currentDateTime WHERE r.deviceId = :deviceId")
    void updateLastOnline(@Param("deviceId") Long deviceId, @Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT r.sentinelId FROM RelayDevice r WHERE r.deviceId = :deviceId")
    Long findSentinelIdByDeviceId(@Param("deviceId") Long deviceId);

}