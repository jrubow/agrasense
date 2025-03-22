package com.asterlink.rest.repository;

import com.asterlink.rest.model.RelayDevice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface for Relay repository
 * @author jrubow
 */

public interface RelayDeviceRepository extends JpaRepository<RelayDevice, Integer> {
    // @Modifying
    // @Transactional
    // @Query("UPDATE User u SET u.login_attempts = 0 WHERE u.username = :username")
    // void resetLoginAttempts(String username);

    // @Query("SELECT u.login_attempts FROM User u WHERE u.username = :username")
    // int getLoginAttempts(String username);
}