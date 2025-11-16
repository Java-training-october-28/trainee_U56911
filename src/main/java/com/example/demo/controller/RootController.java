package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class RootController {

    @GetMapping("/")
    public String getRoot() {
        // Spring Boot will automatically serve index.html from static resources
        return "forward:/index.html";
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
