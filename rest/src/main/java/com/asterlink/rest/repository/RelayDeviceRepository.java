package com.asterlink.rest.repository;

import com.asterlink.rest.model.RelayDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for Relay repository
 * @author jrubow
 */

public interface RelayDeviceRepository extends JpaRepository<RelayDevice, Long> {

    @Query("SELECT r FROM RelayDevice r WHERE r.sentinelId = :sentinelId")
    List<RelayDevice> findBySentinelId(long sentinelId);
}