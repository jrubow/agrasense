package com.asterlink.rest.repository;

import com.asterlink.rest.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for Instruction.
 * Accesses data from the MySQL table.
 * @author gl3bert
 */

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {

    // Find all instructions for a specific sentinelId.
    @Query("SELECT i FROM Instruction i WHERE i.sentinelId = :sentinelId")
    List<Instruction> findBySentinelId(int sentinelId);

    // Find all instructions for a specific deviceId.
    @Query("SELECT i FROM Instruction i WHERE i.deviceId = :deviceId")
    List<Instruction> findByDeviceId(int deviceId);

    // Find last instruction ID in the table.
    @Query("SELECT MAX(i.instructionId) FROM Instruction i")
    Integer findMaxInstructionId();
}
