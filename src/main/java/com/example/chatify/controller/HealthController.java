package com.example.chatify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP ✅");
        status.put("message", "Chatify backend is running");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}
