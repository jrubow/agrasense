package com.asterlink.rest.repository;

import com.asterlink.rest.model.Account;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Repository interface for Account.
 * Manipulates data in the MySQL table.
 * @author gl3bert
 */

public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Find maximum assigned account ID.
    @Query("SELECT MAX(a.id) FROM Account a")
    public Long findMaxId();

    // Update account last login time.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.lastLogin = :currentDateTime WHERE a.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("currentDateTime") LocalDateTime currentDateTime);

    // Increment login attempts for unsuccessful password entries.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.loginAttempts = a.loginAttempts + 1 WHERE a.id = :id")
    void incrementLoginAttempts(@Param("id") Long id);

    // Reset login attempt for successful password entry.
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.loginAttempts = 0 WHERE a.id = :id")
    void resetLoginAttempts(@Param("id") Long id);

    // Returns false is email is not in the system yet; true if otherwise.
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Account a WHERE a.email = :email")
    boolean existsByEmail(@Param("email") String email);

}
