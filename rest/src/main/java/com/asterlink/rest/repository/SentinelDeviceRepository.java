package com.asterlink.rest.repository;

import com.asterlink.rest.model.SentinelDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for Sentinel repository
 * @author jrubow
 */

public interface SentinelDeviceRepository extends JpaRepository<SentinelDevice, Long> {

    List<SentinelDevice> findByClientId(int clientId);

    @Query("SELECT MAX(r.deviceId) FROM SentinelDevice r")
    Long findMaxDeviceId();
}