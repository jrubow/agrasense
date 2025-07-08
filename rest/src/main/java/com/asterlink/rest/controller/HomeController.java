package com.asterlink.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 */

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/{path:[^\\.]*}"}) // Forward only non-file requests
    public String forwardReactRoutes(HttpServletRequest request) {
        // System.out.println("New Session: " + request.getSession().getId());
        return "forward:/index.html";
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Server active.");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new LinkedHashMap<>();

        status.put("message", "Server active");
        status.put("timestamp", LocalDateTime.now());

        Runtime runtime = Runtime.getRuntime();
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        status.put("availableProcessors", runtime.availableProcessors());
        status.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
        status.put("freeMemoryMB", runtime.freeMemory() / (1024 * 1024));
        status.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));
        status.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        status.put("uptimeMs", runtimeMxBean.getUptime());
        status.put("startupTime", Instant.ofEpochMilli(runtimeMxBean.getStartTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());

        status.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());

        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        status.put("heapInitMB", heapUsage.getInit() / (1024 * 1024));
        status.put("heapUsedMB", heapUsage.getUsed() / (1024 * 1024));
        status.put("heapMaxMB", heapUsage.getMax() / (1024 * 1024));
        status.put("heapCommittedMB", heapUsage.getCommitted() / (1024 * 1024));

        return ResponseEntity.ok(status);
    }
}
