package com.example.demo.controller;

import com.example.demo.dto.KafkaTaskMessageDTO;
import com.example.demo.service.KafkaMessageProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for demonstrating Spring Cloud Stream with Kafka integration
 */
@RestController
@RequestMapping("/api/kafka-demo")
@Tag(name = "Kafka Demo", description = "Endpoints for demonstrating Spring Cloud Stream with Kafka")
public class KafkaDemoController {
    
    private final KafkaMessageProducerService kafkaMessageProducerService;
    
    public KafkaDemoController(KafkaMessageProducerService kafkaMessageProducerService) {
        this.kafkaMessageProducerService = kafkaMessageProducerService;
    }
    
    @PostMapping("/send-test")
    @Operation(summary = "Send a test message to Kafka", 
               description = "Sends a test message to demonstrate Kafka integration")
    public ResponseEntity<Map<String, Object>> sendTestMessage() {
        kafkaMessageProducerService.sendTestMessage();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test message sent to Kafka successfully");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-task-created")
    @Operation(summary = "Send a task created event", 
               description = "Simulates a task creation event and sends it to Kafka")
    public ResponseEntity<Map<String, Object>> sendTaskCreatedEvent(
            @RequestParam String taskTitle,
            @RequestParam Long userId,
            @RequestParam String userName) {
        
        // Generate a random task ID for demo purposes
        Long taskId = Math.abs(UUID.randomUUID().getMostSignificantBits() % 10000);
        
        kafkaMessageProducerService.sendTaskCreatedEvent(taskId, taskTitle, userId, userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task created event sent to Kafka");
        response.put("taskId", taskId);
        response.put("taskTitle", taskTitle);
        response.put("eventType", "TASK_CREATED");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-task-updated")
    @Operation(summary = "Send a task updated event", 
               description = "Simulates a task update event and sends it to Kafka")
    public ResponseEntity<Map<String, Object>> sendTaskUpdatedEvent(
            @RequestParam Long taskId,
            @RequestParam String taskTitle,
            @RequestParam String taskStatus,
            @RequestParam Long userId,
            @RequestParam String userName) {
        
        kafkaMessageProducerService.sendTaskUpdatedEvent(taskId, taskTitle, taskStatus, userId, userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task updated event sent to Kafka");
        response.put("taskId", taskId);
        response.put("taskTitle", taskTitle);
        response.put("taskStatus", taskStatus);
        response.put("eventType", "TASK_UPDATED");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-task-completed")
    @Operation(summary = "Send a task completed event", 
               description = "Simulates a task completion event and sends it to Kafka")
    public ResponseEntity<Map<String, Object>> sendTaskCompletedEvent(
            @RequestParam Long taskId,
            @RequestParam String taskTitle,
            @RequestParam Long userId,
            @RequestParam String userName) {
        
        kafkaMessageProducerService.sendTaskCompletedEvent(taskId, taskTitle, userId, userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Task completed event sent to Kafka");
        response.put("taskId", taskId);
        response.put("taskTitle", taskTitle);
        response.put("eventType", "TASK_COMPLETED");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-custom-event")
    @Operation(summary = "Send a custom event", 
               description = "Sends a custom event with all parameters to Kafka")
    public ResponseEntity<Map<String, Object>> sendCustomEvent(
            @RequestBody KafkaTaskMessageDTO messageDTO) {
        
        kafkaMessageProducerService.sendMessage(messageDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Custom event sent to Kafka");
        response.put("eventType", messageDTO.getEventType());
        response.put("taskId", messageDTO.getTaskId());
        response.put("messageId", messageDTO.getMessageId());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Check Kafka integration health", 
               description = "Returns the status of Kafka integration (always returns healthy for demo)")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Kafka Demo Controller");
        response.put("kafkaIntegration", "ENABLED");
        response.put("springCloudStream", "ACTIVE");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    @Operation(summary = "Get Kafka demo information", 
               description = "Returns information about the Kafka demo endpoints")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Spring Cloud Stream Kafka Demo");
        info.put("description", "Demonstration of Spring Cloud Stream with Kafka integration");
        info.put("endpoints", new String[] {
            "POST /api/kafka-demo/send-test - Send test message",
            "POST /api/kafka-demo/send-task-created - Send task created event",
            "POST /api/kafka-demo/send-task-updated - Send task updated event",
            "POST /api/kafka-demo/send-task-completed - Send task completed event",
            "POST /api/kafka-demo/send-custom-event - Send custom event",
            "GET /api/kafka-demo/health - Health check",
            "GET /api/kafka-demo/info - This info endpoint"
        });
        info.put("kafkaTopic", "task-events");
        info.put("consumerGroup", "task-management-group");
        info.put("technology", "Spring Cloud Stream + Kafka");
        
        return ResponseEntity.ok(info);
    }
}
