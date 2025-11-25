package com.example.demo.controller;

import com.example.demo.service.NotificationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud")
@RefreshScope
public class CloudIntegrationController {

    private final NotificationClientService notificationClientService;

    @Value("${app.feature.dynamic-config:default-value}")
    private String dynamicConfigValue;

    @Value("${app.feature.load-balancing:enabled}")
    private String loadBalancingFeature;

    @Autowired
    public CloudIntegrationController(NotificationClientService notificationClientService) {
        this.notificationClientService = notificationClientService;
    }

    /**
     * Test endpoint to verify configuration refresh
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("dynamicConfigValue", dynamicConfigValue);
        config.put("loadBalancingFeature", loadBalancingFeature);
        config.put("message", "Configuration loaded from Config Server");
        return ResponseEntity.ok(config);
    }

    /**
     * Test service discovery functionality
     */
    @GetMapping("/discovery")
    public ResponseEntity<String> testServiceDiscovery() {
        String discoveryInfo = notificationClientService.checkServiceHealth("task-management-service");
        return ResponseEntity.ok(discoveryInfo);
    }

    /**
     * Test load-balanced service communication
     */
    @PostMapping("/notify")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }

        String result = notificationClientService.sendNotification(message);
        return ResponseEntity.ok(result);
    }

    /**
     * Health check for Spring Cloud components
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> cloudHealth() {
        Map<String, String> health = new HashMap<>();
        
        // Check Config Server connectivity
        health.put("configServer", "Configured to: http://localhost:8888");
        health.put("configValue", dynamicConfigValue);
        
        // Check Eureka connectivity
        String eurekaInfo = notificationClientService.checkServiceHealth("task-management-service");
        health.put("eurekaRegistration", eurekaInfo.contains("not found") ? "NOT_REGISTERED" : "REGISTERED");
        health.put("serviceInstances", eurekaInfo);
        
        // Check load balancing
        health.put("loadBalancing", loadBalancingFeature);
        
        return ResponseEntity.ok(health);
    }

    /**
     * Test configuration refresh (requires POST to /actuator/refresh)
     * This endpoint shows current values that can be refreshed
     */
    @GetMapping("/refresh-info")
    public ResponseEntity<Map<String, String>> getRefreshInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("currentDynamicConfig", dynamicConfigValue);
        info.put("currentLoadBalancing", loadBalancingFeature);
        info.put("refreshEndpoint", "POST /actuator/refresh to refresh configuration");
        info.put("note", "Configuration changes require pushing to Git repository first");
        return ResponseEntity.ok(info);
    }
}
