package com.asterlink.rest.service;

import com.asterlink.rest.model.Instruction;

import java.util.List;

/**
 * Service interface for Instruction
 * @author gl3bert
 */

public interface InstructionService {
    boolean createInstruction(Instruction instruction);
    boolean deleteInstruction(int instructionId);
    List<Instruction> getInstructionsBySentinelId(long sentinelId);
    List<Instruction> getInstructionsByDeviceId(long deviceId);
}
