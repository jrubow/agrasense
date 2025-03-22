package com.asterlink.rest.service.impl;

import com.asterlink.rest.model.Instruction;
import com.asterlink.rest.repository.InstructionRepository;
import com.asterlink.rest.service.InstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation for Instruction service.
 * @author gl3bert
 */

@Service
public class InstructionServiceImpl implements InstructionService {

    // Set up repository access.
    private final InstructionRepository instructionRepository;
    public InstructionServiceImpl(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    // Create new instruction entry.
    @Override
    public boolean createInstruction(Instruction instruction) {
        try {
            instruction.setInstructionId(getNextInstructionId());
            instructionRepository.save(instruction);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete instruction entry by its ID.
    @Override
    public boolean deleteInstruction(int instructionId) {
        try {
            instructionRepository.deleteById(instructionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return list of instructions for a specific sentinelId
    @Override
    public List<Instruction> getInstructionsBySentinelId(long sentinelId) {
        return instructionRepository.findBySentinelId(sentinelId);
    }

    // Return list of instructions for a specific deviceId
    @Override
    public List<Instruction> getInstructionsByDeviceId(long deviceId) {
        return instructionRepository.findByDeviceId(deviceId);
    }

    // Get ID for a new record entry.
    public int getNextInstructionId() {
        Integer maxId = instructionRepository.findMaxInstructionId();
        return (maxId == null) ? 1 : maxId + 1;
    }
}
