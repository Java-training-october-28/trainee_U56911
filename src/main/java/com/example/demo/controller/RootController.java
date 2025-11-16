package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getRoot() {
        return ResponseEntity.ok(Map.of(
            "message", "Training Reference API is running",
            "status", "healthy",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Training Reference API",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
