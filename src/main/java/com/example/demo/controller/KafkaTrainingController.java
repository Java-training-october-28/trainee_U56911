package com.example.demo.controller;

import com.example.demo.dto.KafkaTaskMessageDTO;
import com.example.demo.service.EnhancedKafkaConsumerService;
import com.example.demo.service.EnhancedKafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/kafka-training")
@Tag(name = "Kafka Training", description = "Endpoints for demonstrating Kafka concepts to trainees")
public class KafkaTrainingController {

    private final EnhancedKafkaProducerService producerService;
    private final EnhancedKafkaConsumerService consumerService;

    // ==================== STATIC REFERENCE DATA ====================

    private static final String[] BASICS_TOPICS = {
        "Topics: Logical channels", "Partitions: Parallel units",
        "Producers: Send messages", "Consumers: Read messages", "Consumer Groups: Coordinate consumers"
    };

    private static final String[] PRODUCER_PATTERNS = {
        "Synchronous: Blocking send", "Asynchronous: Non-blocking with callbacks",
        "Transactional: Atomic groups", "Fire-and-Forget: No ack"
    };

    private static final String[] CONSUMER_PATTERNS = {
        "@KafkaListener with manual ack", "Async: Non-blocking processing",
        "Batch: Multiple messages at once", "Consumer Groups: Parallel processing"
    };

    private static final String[] CONSUMER_CONFIGS = {
        "AUTO_OFFSET_RESET: earliest|latest|none", "ENABLE_AUTO_COMMIT: true/false",
        "MAX_POLL_RECORDS: batch size", "SESSION_TIMEOUT_MS: heartbeat timeout"
    };

    private static final String[] EDA_PATTERNS = {
        "Domain Events: Business events as messages", "Event Sourcing: State as sequence of events",
        "CQRS: Separate read/write models", "Event Carried State Transfer: Include state in events"
    };

    private static final String[] EVENT_CHARACTERISTICS = {
        "Immutable", "Ordered", "Durable", "Replayable"
    };

    private static final String[] ERROR_PATTERNS = {
        "Retry with Exponential Backoff", "Dead Letter Topics (DLQ)",
        "Circuit Breaker", "Bulkhead"
    };

    private static final String[] STREAM_FEATURES = {
        "Functional Model: Supplier/Function/Consumer", "Binder Abstraction: Kafka, RabbitMQ",
        "Declarative Config: application.yaml", "Content Negotiation: JSON, Avro, Protobuf"
    };

    private static final String[] PARTITION_CONCEPTS = {
        "Partitions: Parallelism unit", "Replication Factor: Fault tolerance copies",
        "Partition Key: Controls routing", "Consumer Assignment: Partition-to-consumer mapping"
    };

    private static final String[] PARTITION_STRATEGIES = {
        "Round Robin: Even distribution", "Key-based: Same key → same partition",
        "Custom Partitioner: Business logic"
    };

    private static final String[] PRODUCER_CONFIGS = {
        "batch.size: 16KB-1MB", "linger.ms: 0-100ms",
        "compression.type: none|gzip|snappy|lz4", "buffer.memory: 32MB default"
    };

    private static final String[] CONSUMER_PERF_CONFIGS = {
        "fetch.min.bytes: 1 byte", "fetch.max.wait.ms: 500ms",
        "max.partition.fetch.bytes: 1MB", "max.poll.records: 500"
    };

    private static final String[] MONITORING_AREAS = {
        "Producer: send rate, error rate, latency", "Consumer: lag, throughput, rebalances",
        "Broker: disk, network, CPU", "Topic: message rate, size, partitions"
    };

    private static final String[] OBSERVABILITY_TOOLS = {
        "Spring Boot Actuator: /actuator/metrics", "Micrometer", "Prometheus", "Grafana", "JMX"
    };

    private static final String[] SUMMARY_CONCEPTS = {
        "Kafka Basics: Topics, Partitions, Producers, Consumers",
        "Event-Driven Architecture: Domain Events, CQRS",
        "Producer Patterns: Sync, Async, Transactional",
        "Consumer Patterns: Traditional, Async, Batch",
        "Spring Cloud Stream: Functional Programming Model",
        "Error Handling: Retries, DLQ, Circuit Breakers",
        "Performance: Batching, Compression, Tuning",
        "Monitoring: Metrics, Logging, Health Checks"
    };

    private static final String[] UNDERSTANDING_QUESTIONS = {
        "What is the difference between a topic and a partition?",
        "How does Kafka ensure message ordering?",
        "What is a consumer group and how does it work?",
        "When would you use synchronous vs asynchronous producer?",
        "What is a Dead Letter Topic and when is it used?",
        "How does Spring Cloud Stream simplify Kafka integration?",
        "What metrics would you monitor in a Kafka cluster?",
        "How can you ensure exactly-once message processing?"
    };

    // ==================== CONSTRUCTOR ====================

    public KafkaTrainingController(EnhancedKafkaProducerService producerService,
                                   EnhancedKafkaConsumerService consumerService) {
        this.producerService = producerService;
        this.consumerService = consumerService;
    }

    // ==================== ENDPOINTS ====================

    @PostMapping("/demonstrate-basics")
    @Operation(summary = "Demonstrate basic Kafka concepts")
    public ResponseEntity<Map<String, Object>> demonstrateBasics() {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO("TRAINING_EVENT", 999L, "Training Task", "IN_PROGRESS", 1L, "Trainer");
        producerService.sendMessageSynchronous("task-events", message);
        return ResponseEntity.ok(Map.of(
            "concept", "Kafka Basics",
            "topics", BASICS_TOPICS,
            "messageSent", message
        ));
    }

    @PostMapping("/demonstrate-producers")
    @Operation(summary = "Demonstrate producer patterns: sync, async, headers")
    public ResponseEntity<Map<String, Object>> demonstrateProducers() {
        KafkaTaskMessageDTO message = createTrainingMessage("PRODUCER_DEMO");
        var syncResult = producerService.sendMessageSynchronous("task-events", message);
        CompletableFuture<?> asyncFuture = producerService.sendMessageAsynchronous("task-events", message);
        producerService.sendMessageWithHeaders("task-events", message);
        return ResponseEntity.ok(Map.of(
            "concept", "Producer Patterns",
            "patterns", PRODUCER_PATTERNS,
            "syncResult", Map.of("partition", syncResult.getRecordMetadata().partition(),
                                 "offset",    syncResult.getRecordMetadata().offset()),
            "asyncStatus", "initiated"
        ));
    }

    @GetMapping("/demonstrate-consumers")
    @Operation(summary = "Demonstrate consumer patterns: listener, async, batch")
    public ResponseEntity<Map<String, Object>> demonstrateConsumers() {
        for (int i = 1; i <= 5; i++)
            producerService.sendMessageAsynchronous("task-events",
                new KafkaTaskMessageDTO("TRAINING_EVENT", (long) i, "Training Task " + i, "TODO", 1L, "Trainee"));
        return ResponseEntity.ok(Map.of(
            "concept", "Consumer Patterns",
            "patterns", CONSUMER_PATTERNS,
            "configs", CONSUMER_CONFIGS,
            "messagesSent", 5
        ));
    }

    @PostMapping("/demonstrate-event-driven")
    @Operation(summary = "Demonstrate event-driven architecture: domain events, CQRS")
    public ResponseEntity<Map<String, Object>> demonstrateEventDriven() {
        producerService.publishTaskCreatedEvent(100L, "Learn Kafka", 1L, "Trainee");
        producerService.publishTaskUpdatedEvent(100L, "Learn Kafka", "IN_PROGRESS", 1L, "Trainee");
        producerService.publishTaskCompletedEvent(100L, "Learn Kafka", 1L, "Trainee");
        return ResponseEntity.ok(Map.of(
            "concept", "Event-Driven Architecture",
            "patterns", EDA_PATTERNS,
            "eventsPublished", new String[]{"task.created", "task.updated", "task.completed"},
            "characteristics", EVENT_CHARACTERISTICS
        ));
    }

    @PostMapping("/demonstrate-error-handling")
    @Operation(summary = "Demonstrate error handling: retries, DLQ, circuit breaker")
    public ResponseEntity<Map<String, Object>> demonstrateErrorHandling() {
        producerService.sendWithRetry("task-events", createTrainingMessage("RETRY_DEMO"), 3);
        for (int i = 0; i < 3; i++)
            producerService.sendMessageAsynchronous("task-events", createTrainingMessage("TEST_FAILURE_" + i));
        return ResponseEntity.ok(Map.of(
            "concept", "Error Handling & Resilience",
            "patterns", ERROR_PATTERNS,
            "note", "Check logs for retry attempts and DLQ processing"
        ));
    }

    @GetMapping("/demonstrate-spring-cloud-stream")
    @Operation(summary = "Demonstrate Spring Cloud Stream: functional model, binder")
    public ResponseEntity<Map<String, Object>> demonstrateSpringCloudStream() {
        for (int i = 1; i <= 3; i++)
            producerService.sendMessageAsynchronous("task-events",
                new KafkaTaskMessageDTO("SPRING_CLOUD_STREAM_DEMO", (long) i, "SCS Task " + i, "TODO", 1L, "Trainee"));
        return ResponseEntity.ok(Map.of(
            "concept", "Spring Cloud Stream",
            "features", STREAM_FEATURES,
            "configExample", "spring.cloud.stream.bindings.taskOutput.destination=task-events",
            "messagesSent", 3
        ));
    }

    @GetMapping("/demonstrate-topics-partitions")
    @Operation(summary = "Demonstrate topics, partitions, and replication")
    public ResponseEntity<Map<String, Object>> demonstrateTopicsPartitions() {
        KafkaTaskMessageDTO m1 = createTrainingMessage("PARTITION_DEMO_1");
        KafkaTaskMessageDTO m2 = createTrainingMessage("PARTITION_DEMO_2");
        KafkaTaskMessageDTO m3 = createTrainingMessage("PARTITION_DEMO_3");
        producerService.sendWithPartitionKey("task-events", "user-123", m1);
        producerService.sendWithPartitionKey("task-events", "user-123", m2);
        producerService.sendWithPartitionKey("task-events", "user-456", m3);
        producerService.sendWithCustomPartitioning("task-events", m1);
        return ResponseEntity.ok(Map.of(
            "concept", "Topics & Partitions",
            "keyConcepts", PARTITION_CONCEPTS,
            "strategies", PARTITION_STRATEGIES
        ));
    }

    @GetMapping("/demonstrate-performance")
    @Operation(summary = "Demonstrate performance tuning: batching, compression")
    public ResponseEntity<Map<String, Object>> demonstratePerformance() {
        for (int i = 1; i <= 10; i++)
            producerService.sendMessageAsynchronous("task-events",
                new KafkaTaskMessageDTO("PERFORMANCE_DEMO", (long) i, "Perf Task " + i, "TODO", 1L, "Trainee"));
        return ResponseEntity.ok(Map.of(
            "concept", "Performance & Optimization",
            "producerConfigs", PRODUCER_CONFIGS,
            "consumerConfigs", CONSUMER_PERF_CONFIGS,
            "batchSent", 10
        ));
    }

    @GetMapping("/demonstrate-monitoring")
    @Operation(summary = "Demonstrate monitoring: metrics, lag, health checks")
    public ResponseEntity<Map<String, Object>> demonstrateMonitoring() {
        return ResponseEntity.ok(Map.of(
            "concept", "Monitoring & Observability",
            "areas", MONITORING_AREAS,
            "tools", OBSERVABILITY_TOOLS,
            "actuatorEndpoint", "/actuator/metrics"
        ));
    }

    @GetMapping("/summary")
    @Operation(summary = "Kafka training summary")
    public ResponseEntity<Map<String, Object>> getTrainingSummary() {
        return ResponseEntity.ok(Map.of(
            "title", "Kafka Comprehensive Training",
            "audience", "Java/Spring Boot Developers",
            "duration", "2-3 hours",
            "conceptsCovered", SUMMARY_CONCEPTS
        ));
    }

    @PostMapping("/send-training-message")
    @Operation(summary = "Send a practice message to Kafka")
    public ResponseEntity<Map<String, Object>> sendTrainingMessage(
            @RequestParam(defaultValue = "TRAINING_EVENT") String eventType,
            @RequestParam(defaultValue = "Practice Task") String taskTitle) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            eventType, System.currentTimeMillis() % 10000, taskTitle, "TODO", 1L, "Trainee");
        producerService.sendMessageAsynchronous("task-events", message);
        return ResponseEntity.ok(Map.of("status", "sent", "message", message));
    }

    @GetMapping("/check-understanding")
    @Operation(summary = "Quiz questions to test Kafka understanding")
    public ResponseEntity<Map<String, Object>> checkUnderstanding() {
        return ResponseEntity.ok(Map.of(
            "questions", UNDERSTANDING_QUESTIONS,
            "exercise", "Implement a retry mechanism for failed messages",
            "challenge", "Build a simple event sourcing system using Kafka"
        ));
    }

    // ==================== HELPERS ====================

    private KafkaTaskMessageDTO createTrainingMessage(String eventType) {
        return new KafkaTaskMessageDTO(eventType, System.currentTimeMillis() % 1000,
            "Training: " + eventType, "IN_PROGRESS", 1L, "Kafka Trainee");
    }
}
