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

    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice s SET s.longitude = :longitude, s.latitude = :latitude WHERE s.deviceId = :deviceId")
    void updateLocation(@Param("deviceId") Long deviceId, @Param("longitude") double latitude, @Param("latitude") double longitude);

    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice r SET r.deployedDate = :currentDateTime WHERE r.deviceId = :deviceId")
    void setDeployedDate(@Param("deviceId") Long deviceId, @Param("currentDateTime") LocalDateTime currentDateTime);

    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice r SET r.deployed = :deployed WHERE r.deviceId = :deviceId")
    void setDeployedStatus(@Param("deviceId") Long deviceId, @Param("currentDateTime") boolean deployed);

    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice r SET r.numConnectedDevices = r.numConnectedDevices + 1 WHERE r.deviceId = :deviceId")
    void incrementNumConnectedDevices(@Param("deviceId") Long deviceId);
    
    @Modifying
    @Transactional
    @Query("UPDATE SentinelDevice s SET s.batteryLife = :battery WHERE s.deviceId = :deviceId")
    void updateBattery(@Param("deviceId") Long deviceId, @Param("battery") double battery);
}