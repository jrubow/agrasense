package com.asterlink.rest.controller;

import com.asterlink.rest.model.Instruction;
import com.asterlink.rest.service.InstructionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Intstruction class.
 * Handles API requests.
 * @author Gleb Bereziuk (gl3bert)
 */

@RestController
@RequestMapping("api/instruction")
public class InstructionController {

    // Set up service access.
    InstructionService instructionService;
    public InstructionController(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    // Add new record.
    @PostMapping
    public String createInstruction(@RequestBody Instruction instruction) {
        instructionService.createInstruction(instruction);
        return "Instruction created.";
    }

    // Delete instruction by its ID.
    @PostMapping("/delete/{instructionId}")
    public String deleteInstruction(@PathVariable("instructionId") int instructionId) {
        instructionService.deleteInstruction(instructionId);
        return "Instruction deleted.";
    }

    // Send instructions by sentinelId.
    @GetMapping("/get/sentinel/{sentinelId}")
    public List<Instruction> getInstructionsBySentinelId(@PathVariable("sentinelId") int sentinelId) {
        return instructionService.getInstructionsBySentinelId(sentinelId);
    }

    // Send instructions by deviceId.
    @GetMapping("/get/device/{deviceId}")
    public List<Instruction> getInstructionsByDeviceId(@PathVariable("deviceId") int deviceId) {
        return instructionService.getInstructionsByDeviceId(deviceId);
    }

}
