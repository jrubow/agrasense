package com.asterlink.rest.repository;

import com.asterlink.rest.model.SentinelDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for Sentinel repository
 * @author jrubow
 */

public interface SentinelDeviceRepository extends JpaRepository<SentinelDevice, Integer> {

    // @Modifying
    // @Transactional
    // @Query("UPDATE User u SET u.login_attempts = 0 WHERE u.username = :username")
    // void resetLoginAttempts(String username);

    // @Query("SELECT u.login_attempts FROM User u WHERE u.username = :username")
    // int getLoginAttempts(String username);
    List<SentinelDevice> findByClientId(int clientId);

    @Query("SELECT MAX(r.deviceId) FROM SentinelDevice r")
    Integer findMaxDeviceId();
}