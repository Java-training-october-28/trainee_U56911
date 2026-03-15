package com.example.demo.controller;

import com.example.demo.dto.KafkaTaskMessageDTO;
import com.example.demo.service.EnhancedKafkaConsumerService;
import com.example.demo.service.KafkaStreamBridgeProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Demonstrates Spring Cloud Stream (StreamBridge) Kafka integration.
 * For raw KafkaTemplate patterns see KafkaTrainingController.
 */
@RestController
@RequestMapping("/api/kafka-demo")
@Tag(name = "Kafka StreamBridge Demo", description = "Spring Cloud Stream / StreamBridge send & search")
public class KafkaStreamBridgeDemoController {

    private final KafkaStreamBridgeProducerService producer;
    private final EnhancedKafkaConsumerService consumer;

    public KafkaStreamBridgeDemoController(KafkaStreamBridgeProducerService producer,
                                           EnhancedKafkaConsumerService consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @PostMapping("/send-test")
    @Operation(summary = "Send a test message via StreamBridge")
    public ResponseEntity<Map<String, Object>> sendTestMessage() {
        producer.sendTestMessage();
        return ResponseEntity.ok(Map.of("success", true, "timestamp", System.currentTimeMillis()));
    }

    @PostMapping("/send-task-created")
    @Operation(summary = "Send TASK_CREATED event")
    public ResponseEntity<Map<String, Object>> sendTaskCreatedEvent(
            @RequestParam String taskTitle,
            @RequestParam Long userId,
            @RequestParam String userName) {
        Long taskId = randomId();
        producer.sendTaskCreatedEvent(taskId, taskTitle, userId, userName);
        return ResponseEntity.ok(Map.of("taskId", taskId, "taskTitle", taskTitle, "eventType", "TASK_CREATED"));
    }

    @PostMapping("/send-task-updated")
    @Operation(summary = "Send TASK_UPDATED event")
    public ResponseEntity<Map<String, Object>> sendTaskUpdatedEvent(
            @RequestParam Long taskId,
            @RequestParam String taskTitle,
            @RequestParam String taskStatus,
            @RequestParam Long userId,
            @RequestParam String userName) {
        producer.sendTaskUpdatedEvent(taskId, taskTitle, taskStatus, userId, userName);
        return ResponseEntity.ok(Map.of("taskId", taskId, "taskTitle", taskTitle, "taskStatus", taskStatus, "eventType", "TASK_UPDATED"));
    }

    @PostMapping("/send-task-completed")
    @Operation(summary = "Send TASK_COMPLETED event")
    public ResponseEntity<Map<String, Object>> sendTaskCompletedEvent(
            @RequestParam Long taskId,
            @RequestParam String taskTitle,
            @RequestParam Long userId,
            @RequestParam String userName) {
        producer.sendTaskCompletedEvent(taskId, taskTitle, userId, userName);
        return ResponseEntity.ok(Map.of("taskId", taskId, "taskTitle", taskTitle, "eventType", "TASK_COMPLETED"));
    }

    @PostMapping("/send-custom-event")
    @Operation(summary = "Send a custom event payload")
    public ResponseEntity<Map<String, Object>> sendCustomEvent(@RequestBody KafkaTaskMessageDTO messageDTO) {
        producer.sendMessage(messageDTO);
        return ResponseEntity.ok(Map.of(
            "eventType", messageDTO.getEventType(),
            "taskId", messageDTO.getTaskId(),
            "messageId", messageDTO.getMessageId()
        ));
    }

    @GetMapping("/health")
    @Operation(summary = "StreamBridge Kafka health check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "KafkaStreamBridgeDemoController",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/messages/search")
    @Operation(summary = "Search in-memory consumed messages by keyword")
    public ResponseEntity<Map<String, Object>> searchMessages(
            @RequestParam(defaultValue = "") String keyword) {
        List<KafkaTaskMessageDTO> results = keyword.isBlank()
            ? consumer.getAllMessages()
            : consumer.searchMessages(keyword);
        return ResponseEntity.ok(Map.of("keyword", keyword, "count", results.size(), "messages", results));
    }

    @PostMapping("/messages/send")
    @Operation(summary = "Send a message and confirm delivery")
    public ResponseEntity<Map<String, Object>> sendAndConfirm(
            @RequestParam String taskTitle,
            @RequestParam Long userId,
            @RequestParam String userName,
            @RequestParam(defaultValue = "TASK_CREATED") String eventType) {
        Long taskId = randomId();
        switch (eventType.toUpperCase()) {
            case "TASK_UPDATED"   -> producer.sendTaskUpdatedEvent(taskId, taskTitle, "IN_PROGRESS", userId, userName);
            case "TASK_COMPLETED" -> producer.sendTaskCompletedEvent(taskId, taskTitle, userId, userName);
            default               -> producer.sendTaskCreatedEvent(taskId, taskTitle, userId, userName);
        }
        return ResponseEntity.ok(Map.of(
            "taskId", taskId, "taskTitle", taskTitle,
            "eventType", eventType.toUpperCase(), "userId", userId, "userName", userName
        ));
    }

    private static long randomId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits() % 10000);
    }
}
