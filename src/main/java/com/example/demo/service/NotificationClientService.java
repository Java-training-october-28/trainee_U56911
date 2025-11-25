package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NotificationClientService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public NotificationClientService(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Example of service discovery using DiscoveryClient
     * This method demonstrates how to discover services manually
     */
    public List<ServiceInstance> discoverNotificationService() {
        return discoveryClient.getInstances("notification-service");
    }

    /**
     * Example of load-balanced service-to-service communication
     * This would call a notification service using service discovery
     */
    public String sendNotification(String message) {
        try {
            // Using service name instead of direct URL (load balanced)
            String notificationServiceUrl = "http://notification-service/api/notifications/send";
            
            // Create notification request
            NotificationRequest request = new NotificationRequest(message, "SYSTEM");
            
            // Make the call using load-balanced RestTemplate
            ResponseEntity<String> response = restTemplate.postForEntity(
                notificationServiceUrl, request, String.class);
            
            return response.getBody();
        } catch (Exception e) {
            // Fallback: log and return error message
            return "Notification service unavailable: " + e.getMessage();
        }
    }

    /**
     * Example of health check for discovered services
     */
    public String checkServiceHealth(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            return "Service " + serviceName + " not found in registry";
        }
        
        StringBuilder healthInfo = new StringBuilder();
        healthInfo.append("Service: ").append(serviceName).append("\n");
        healthInfo.append("Instances: ").append(instances.size()).append("\n");
        
        for (ServiceInstance instance : instances) {
            healthInfo.append(" - Instance: ")
                     .append(instance.getInstanceId())
                     .append(" at ")
                     .append(instance.getUri())
                     .append("\n");
        }
        
        return healthInfo.toString();
    }

    // Inner class for notification request
    public static class NotificationRequest {
        private String message;
        private String type;

        public NotificationRequest() {}

        public NotificationRequest(String message, String type) {
            this.message = message;
            this.type = type;
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
