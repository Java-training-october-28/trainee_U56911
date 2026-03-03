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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka Training Controller for Demonstrating Kafka Concepts to Trainees
 * 
 * This controller provides endpoints to demonstrate:
 * 1. Event-driven communication patterns
 * 2. Kafka topics, partitions, replication
 * 3. Producer and Consumer implementation
 * 4. Async Kafka listener
 * 5. Spring Cloud Stream integration
 * 6. Dead Letter Topic handling
 * 7. Failure handling and retries
 */
@RestController
@RequestMapping("/api/kafka-training")
@Tag(name = "Kafka Training", description = "Endpoints for demonstrating Kafka concepts to trainees")
public class KafkaTrainingController {
    
    private final EnhancedKafkaProducerService producerService;
    private final EnhancedKafkaConsumerService consumerService;
    
    public KafkaTrainingController(
            EnhancedKafkaProducerService producerService,
            EnhancedKafkaConsumerService consumerService) {
        this.producerService = producerService;
        this.consumerService = consumerService;
    }
    
    // ==================== BASIC KAFKA CONCEPTS ====================
    
    @PostMapping("/demonstrate-basics")
    @Operation(summary = "Demonstrate basic Kafka concepts",
               description = "Shows topics, partitions, producers, and consumers")
    public ResponseEntity<Map<String, Object>> demonstrateBasics() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Kafka Basics");
        response.put("topics", new String[] {
            "1. Topics: Logical channels for messages",
            "2. Partitions: Parallel units within topics",
            "3. Producers: Send messages to topics",
            "4. Consumers: Read messages from topics",
            "5. Consumer Groups: Coordinate consumers"
        });
        
        response.put("example", "Sending a basic message...");
        
        // Send a basic message
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TRAINING_EVENT", 
            999L, 
            "Training Task", 
            "IN_PROGRESS", 
            1L, 
            "Trainer"
        );
        
        producerService.sendMessageSynchronous("task-events", message);
        
        response.put("status", "Basic concepts demonstrated");
        response.put("messageSent", message);
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== PRODUCER PATTERNS ====================
    
    @PostMapping("/demonstrate-producers")
    @Operation(summary = "Demonstrate different producer patterns",
               description = "Shows synchronous, asynchronous, and transactional producers")
    public ResponseEntity<Map<String, Object>> demonstrateProducers() {
        Map<String, Object> response = new HashMap<>();
        
        KafkaTaskMessageDTO message = createTrainingMessage("PRODUCER_DEMO");
        
        response.put("concept", "Producer Patterns");
        response.put("patterns", new String[] {
            "1. Synchronous Producer: Blocking send with timeout",
            "2. Asynchronous Producer: Non-blocking with callbacks",
            "3. Transactional Producer: Atomic message groups",
            "4. Fire-and-Forget: No acknowledgment"
        });
        
        // Demonstrate synchronous producer
        response.put("synchronousExample", "Sending synchronously...");
        var syncResult = producerService.sendMessageSynchronous("task-events", message);
        response.put("syncResult", Map.of(
            "partition", syncResult.getRecordMetadata().partition(),
            "offset", syncResult.getRecordMetadata().offset()
        ));
        
        // Demonstrate asynchronous producer
        response.put("asynchronousExample", "Sending asynchronously...");
        CompletableFuture<?> asyncFuture = producerService.sendMessageAsynchronous("task-events", message);
        response.put("asyncStatus", "Async send initiated");
        
        // Demonstrate headers
        response.put("headersExample", "Sending with custom headers...");
        producerService.sendMessageWithHeaders("task-events", message);
        
        response.put("status", "Producer patterns demonstrated");
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== CONSUMER PATTERNS ====================
    
    @GetMapping("/demonstrate-consumers")
    @Operation(summary = "Demonstrate different consumer patterns",
               description = "Shows traditional, async, and batch consumers")
    public ResponseEntity<Map<String, Object>> demonstrateConsumers() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Consumer Patterns");
        response.put("patterns", new String[] {
            "1. Traditional Consumer: @KafkaListener with manual ack",
            "2. Async Consumer: Non-blocking processing",
            "3. Batch Consumer: Process multiple messages at once",
            "4. Consumer Groups: Parallel processing"
        });
        
        response.put("consumerConfigurations", new String[] {
            "• AUTO_OFFSET_RESET: earliest, latest, none",
            "• ENABLE_AUTO_COMMIT: true/false for manual control",
            "• MAX_POLL_RECORDS: Control batch size",
            "• SESSION_TIMEOUT_MS: Heartbeat timeout"
        });
        
        // Send messages that will be consumed by different listeners
        for (int i = 1; i <= 5; i++) {
            KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
                "TRAINING_EVENT",
                (long) i,
                "Training Task " + i,
                "TODO",
                1L,
                "Trainee"
            );
            producerService.sendMessageAsynchronous("task-events", message);
        }
        
        response.put("status", "Consumer patterns explained - messages sent for processing");
        response.put("messagesSent", 5);
        response.put("note", "Check application logs to see different consumer patterns in action");
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== EVENT-DRIVEN ARCHITECTURE ====================
    
    @PostMapping("/demonstrate-event-driven")
    @Operation(summary = "Demonstrate event-driven communication",
               description = "Shows domain events, event sourcing, and CQRS patterns")
    public ResponseEntity<Map<String, Object>> demonstrateEventDriven() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Event-Driven Architecture");
        response.put("patterns", new String[] {
            "1. Domain Events: Business events as messages",
            "2. Event Sourcing: State as sequence of events",
            "3. CQRS: Separate read/write models",
            "4. Event Carried State Transfer: Include state in events"
        });
        
        // Demonstrate domain events
        response.put("domainEvents", "Publishing domain events...");
        
        producerService.publishTaskCreatedEvent(100L, "Learn Kafka", 1L, "Trainee");
        producerService.publishTaskUpdatedEvent(100L, "Learn Kafka", "IN_PROGRESS", 1L, "Trainee");
        producerService.publishTaskCompletedEvent(100L, "Learn Kafka", 1L, "Trainee");
        
        response.put("eventsPublished", new String[] {
            "task.created",
            "task.updated", 
            "task.completed"
        });
        
        response.put("eventCharacteristics", new String[] {
            "• Immutable: Events cannot be changed",
            "• Ordered: Sequence matters for state reconstruction",
            "• Durable: Stored permanently in Kafka",
            "• Replayable: Can be reprocessed if needed"
        });
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== ERROR HANDLING & RESILIENCE ====================
    
    @PostMapping("/demonstrate-error-handling")
    @Operation(summary = "Demonstrate error handling and resilience patterns",
               description = "Shows retries, dead letter topics, and circuit breakers")
    public ResponseEntity<Map<String, Object>> demonstrateErrorHandling() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Error Handling & Resilience");
        response.put("patterns", new String[] {
            "1. Retry with Exponential Backoff",
            "2. Dead Letter Topics (DLQ)",
            "3. Circuit Breaker Pattern",
            "4. Bulkhead Pattern"
        });
        
        // Send messages that will trigger different error scenarios
        KafkaTaskMessageDTO retryMessage = createTrainingMessage("RETRY_DEMO");
        KafkaTaskMessageDTO dlqMessage = createTrainingMessage("DLQ_DEMO");
        KafkaTaskMessageDTO circuitBreakerMessage = createTrainingMessage("CIRCUIT_BREAKER_DEMO");
        
        response.put("demonstrations", new String[] {
            "1. Retry Logic: Message will be retried 3 times",
            "2. DLQ Handling: Failed messages go to .DLQ topic",
            "3. Circuit Breaker: Simulates OPEN/CLOSED/HALF_OPEN states"
        });
        
        // Demonstrate retry logic
        producerService.sendWithRetry("task-events", retryMessage, 3);
        
        // Send messages for DLQ demonstration (some will fail)
        for (int i = 0; i < 3; i++) {
            KafkaTaskMessageDTO message = createTrainingMessage("TEST_FAILURE_" + i);
            producerService.sendMessageAsynchronous("task-events", message);
        }
        
        response.put("status", "Error handling patterns demonstrated");
        response.put("note", "Check logs for retry attempts and DLQ processing");
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== SPRING CLOUD STREAM ====================
    
    @GetMapping("/demonstrate-spring-cloud-stream")
    @Operation(summary = "Demonstrate Spring Cloud Stream integration",
               description = "Shows functional programming model with Kafka binder")
    public ResponseEntity<Map<String, Object>> demonstrateSpringCloudStream() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Spring Cloud Stream");
        response.put("keyFeatures", new String[] {
            "1. Functional Programming Model: Supplier/Function/Consumer",
            "2. Binder Abstraction: Kafka, RabbitMQ, etc.",
            "3. Declarative Configuration: application.yaml",
            "4. Content Negotiation: JSON, Avro, Protobuf"
        });
        
        response.put("configurationExample", 
            "spring.cloud.stream.bindings.taskOutput.destination=task-events");
        
        response.put("functionalInterfaces", new String[] {
            "• Supplier<T>: Source of messages",
            "• Function<T, R>: Process and transform",
            "• Consumer<T>: Sink for messages"
        });
        
        // Send messages through Spring Cloud Stream
        for (int i = 1; i <= 3; i++) {
            KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
                "SPRING_CLOUD_STREAM_DEMO",
                (long) i,
                "Spring Cloud Task " + i,
                "TODO",
                1L,
                "Trainee"
            );
            producerService.sendMessageAsynchronous("task-events", message);
        }
        
        response.put("status", "Spring Cloud Stream concepts explained");
        response.put("messagesSent", 3);
        response.put("note", "Messages will be processed by functional consumers");
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== KAFKA TOPICS & PARTITIONS ====================
    
    @GetMapping("/demonstrate-topics-partitions")
    @Operation(summary = "Demonstrate Kafka topics and partitions",
               description = "Shows partitioning strategies and replication")
    public ResponseEntity<Map<String, Object>> demonstrateTopicsPartitions() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Topics & Partitions");
        response.put("keyConcepts", new String[] {
            "1. Partitions: Parallelism unit within a topic",
            "2. Replication Factor: Copies for fault tolerance",
            "3. Partition Key: Controls message routing",
            "4. Consumer Assignment: Which consumer reads which partition"
        });
        
        response.put("partitioningStrategies", new String[] {
            "• Round Robin: Even distribution",
            "• Key-based: Same key → same partition",
            "• Custom Partitioner: Business logic"
        });
        
        // Demonstrate different partitioning strategies
        KafkaTaskMessageDTO message1 = createTrainingMessage("PARTITION_DEMO_1");
        KafkaTaskMessageDTO message2 = createTrainingMessage("PARTITION_DEMO_2");
        KafkaTaskMessageDTO message3 = createTrainingMessage("PARTITION_DEMO_3");
        
        // Same key → same partition
        producerService.sendWithPartitionKey("task-events", "user-123", message1);
        producerService.sendWithPartitionKey("task-events", "user-123", message2);
        
        // Different key → potentially different partition
        producerService.sendWithPartitionKey("task-events", "user-456", message3);
        
        // Custom partitioning
        producerService.sendWithCustomPartitioning("task-events", message1);
        
        response.put("demonstrations", new String[] {
            "1. Key-based partitioning: user-123 messages go to same partition",
            "2. Different keys: user-456 may go to different partition",
            "3. Custom partitioning: Business logic determines partition"
        });
        
        response.put("replicationBenefits", new String[] {
            "• Fault Tolerance: Survive broker failures",
            "• High Availability: Always accessible",
            "• Data Durability: Multiple copies"
        });
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== PERFORMANCE & OPTIMIZATION ====================
    
    @GetMapping("/demonstrate-performance")
    @Operation(summary = "Demonstrate Kafka performance optimizations",
               description = "Shows batching, compression, and tuning parameters")
    public ResponseEntity<Map<String, Object>> demonstratePerformance() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Performance & Optimization");
        response.put("optimizations", new String[] {
            "1. Batching: Group messages for efficiency",
            "2. Compression: Reduce network bandwidth",
            "3. Async Producers: Non-blocking sends",
            "4. Tuned Timeouts: Balance latency vs reliability"
        });
        
        response.put("producerConfigs", new String[] {
            "• batch.size: 16KB-1MB (default: 16KB)",
            "• linger.ms: 0-100ms (default: 0)",
            "• compression.type: none, gzip, snappy, lz4",
            "• buffer.memory: 32MB default"
        });
        
        response.put("consumerConfigs", new String[] {
            "• fetch.min.bytes: 1 byte default",
            "• fetch.max.wait.ms: 500ms default",
            "• max.partition.fetch.bytes: 1MB default",
            "• max.poll.records: 500 default"
        });
        
        // Send batch of messages to demonstrate batching
        response.put("batchDemonstration", "Sending batch of 10 messages...");
        
        for (int i = 1; i <= 10; i++) {
            KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
                "PERFORMANCE_DEMO",
                (long) i,
                "Performance Task " + i,
                "TODO",
                1L,
                "Trainee"
            );
            producerService.sendMessageAsynchronous("task-events", message);
        }
        
        response.put("compressionBenefits", new String[] {
            "• Network: Reduced bandwidth usage",
            "• Storage: Less disk space required",
            "• Cost: Lower cloud storage costs",
            "• Speed: Faster transmission (after compression)"
        });
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== MONITORING & OBSERVABILITY ====================
    
    @GetMapping("/demonstrate-monitoring")
    @Operation(summary = "Demonstrate Kafka monitoring and observability",
               description = "Shows metrics, logging, and health checks")
    public ResponseEntity<Map<String, Object>> demonstrateMonitoring() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("concept", "Monitoring & Observability");
        response.put("monitoringAreas", new String[] {
            "1. Producer Metrics: Send rate, error rate, latency",
            "2. Consumer Metrics: Lag, throughput, rebalances",
            "3. Broker Metrics: Disk usage, network, CPU",
            "4. Topic Metrics: Message rate, size, partitions"
        });
        
        response.put("keyMetrics", new String[] {
            "• Messages per second",
            "• Consumer lag (offset difference)",
            "• Request latency (p50, p95, p99)",
            "• Error rates and retry counts"
        });
        
        response.put("observabilityTools", new String[] {
            "• Spring Boot Actuator: /actuator/metrics",
            "• Micrometer: Standard metrics library",
            "• Prometheus: Time-series database",
            "• Grafana: Visualization dashboard",
            "• JMX: Java Management Extensions"
        });
        
        response.put("healthChecks", new String[] {
            "• Broker connectivity",
            "• Topic availability",
            "• Consumer group health",
            "• Producer health"
        });
        
        response.put("loggingBestPractices", new String[] {
            "• Structured logging (JSON)",
            "• Correlation IDs for tracing",
            "• Appropriate log levels",
            "• Sensitive data masking"
        });
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== HELPER METHODS ====================
    
    private KafkaTaskMessageDTO createTrainingMessage(String eventType) {
        return new KafkaTaskMessageDTO(
            eventType,
            System.currentTimeMillis() % 1000,
            "Training: " + eventType,
            "IN_PROGRESS",
            1L,
            "Kafka Trainee"
        );
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get Kafka training summary",
               description = "Returns a comprehensive summary of all Kafka concepts covered")
    public ResponseEntity<Map<String, Object>> getTrainingSummary() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("trainingTitle", "Kafka Comprehensive Training");
        response.put("targetAudience", "Java/Spring Boot Developers");
        response.put("duration", "2-3 hours hands-on session");
        
        response.put("conceptsCovered", new String[] {
            "1. Kafka Basics: Topics, Partitions, Producers, Consumers",
            "2. Event-Driven Architecture: Domain Events, CQRS",
            "3. Producer Patterns: Sync, Async, Transactional",
            "4. Consumer Patterns: Traditional, Async, Batch",
            "5. Spring Cloud Stream: Functional Programming Model",
            "6. Error Handling: Retries, DLQ, Circuit Breakers",
            "7. Performance: Batching, Compression, Tuning",
            "8. Monitoring: Metrics, Logging, Health Checks"
        });
        
        response.put("handsOnExercises", new String[] {
            "• Send messages using different producer patterns",
            "• Consume messages with various consumer configurations",
            "• Implement error handling with retries and DLQ",
            "• Configure Spring Cloud Stream bindings",
            "• Monitor Kafka metrics and consumer lag"
        });
        
        response.put("prerequisites", new String[] {
            "• Basic Java/Spring Boot knowledge",
            "• Understanding of REST APIs",
            "• Familiarity with messaging concepts"
        });
        
        response.put("learningOutcomes", new String[] {
            "• Design event-driven microservices with Kafka",
            "• Implement reliable producer/consumer patterns",
            "• Handle failures and ensure message delivery",
            "• Monitor and optimize Kafka performance",
            "• Integrate Kafka with Spring Boot applications"
        });
        
        response.put("resources", new String[] {
            "• Kafka Documentation: https://kafka.apache.org/documentation/",
            "• Spring Kafka: https://spring.io/projects/spring-kafka",
            "• Spring Cloud Stream: https://spring.io/projects/spring-cloud-stream",
            "• Confluent Kafka: https://docs.confluent.io/platform/current/"
        });
        
        response.put("nextSteps", new String[] {
            "1. Set up local Kafka cluster using Docker",
            "2. Implement real-world use cases",
            "3. Explore advanced topics: Streams, Connect, Schema Registry",
            "4. Practice with different serialization formats",
            "5. Learn about Kafka security and ACLs"
        });
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== TRAINING UTILITIES ====================
    
    @PostMapping("/send-training-message")
    @Operation(summary = "Send a training message",
               description = "Utility endpoint for trainees to practice sending messages")
    public ResponseEntity<Map<String, Object>> sendTrainingMessage(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String taskTitle) {
        
        Map<String, Object> response = new HashMap<>();
        
        String finalEventType = eventType != null ? eventType : "TRAINING_EVENT";
        String finalTaskTitle = taskTitle != null ? taskTitle : "Practice Task";
        
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            finalEventType,
            System.currentTimeMillis() % 10000,
            finalTaskTitle,
            "TODO",
            1L,
            "Trainee"
        );
        
        producerService.sendMessageAsynchronous("task-events", message);
        
        response.put("status", "Training message sent");
        response.put("message", message);
        response.put("note", "Check application logs to see message processing");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-understanding")
    @Operation(summary = "Check understanding questions",
               description = "Questions to test Kafka understanding")
    public ResponseEntity<Map<String, Object>> checkUnderstanding() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("questions", new String[] {
            "1. What is the difference between a topic and a partition?",
            "2. How does Kafka ensure message ordering?",
            "3. What is a consumer group and how does it work?",
            "4. When would you use synchronous vs asynchronous producer?",
            "5. What is a Dead Letter Topic and when is it used?",
            "6. How does Spring Cloud Stream simplify Kafka integration?",
            "7. What metrics would you monitor in a Kafka cluster?",
            "8. How can you ensure exactly-once message processing?"
        });
        
        response.put("exercise", "Implement a retry mechanism for failed messages");
        response.put("challenge", "Build a simple event sourcing system using Kafka");
        
        return ResponseEntity.ok(response);
    }
}
