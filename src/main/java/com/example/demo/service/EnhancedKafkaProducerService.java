package com.example.demo.service;

import com.example.demo.dto.KafkaTaskMessageDTO;
import com.example.demo.entity.OutboxMessage;
import com.example.demo.repository.OutboxRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.entity.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Enhanced Kafka Producer Service for Training Purposes
 * Demonstrates:
 * 1. Event-driven communication patterns
 * 2. Producer configurations and optimizations
 * 3. Synchronous vs asynchronous sending
 * 4. Transactional producers
 * 5. Custom headers and metadata
 * 6. Error handling and retries
 */
@Service
public class EnhancedKafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedKafkaProducerService.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    
    public EnhancedKafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate,
                                        OutboxRepository outboxRepository,
                                        ObjectMapper objectMapper,
                                        TaskRepository taskRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.taskRepository = taskRepository;
    }
    
    // ==================== BASIC PRODUCER PATTERNS ====================
    
    /**
     * Basic synchronous send with blocking
     * Demonstrates simple fire-and-forget pattern
     */
    public SendResult<String, Object> sendMessageSynchronous(String topic, KafkaTaskMessageDTO message) {
        logger.info("=== SYNCHRONOUS SEND ===");
        logger.info("Sending message synchronously to topic: {}", topic);
        
        try {
            // Training: Synchronous send with timeout
            SendResult<String, Object> result = kafkaTemplate.send(topic, message)
                    .get(10, TimeUnit.SECONDS); // 10 second timeout
            
            logger.info("Message sent successfully: {}", result.getRecordMetadata());
            logProducerMetadata(result);
            
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Send interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to send message", e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("Send timeout after 10 seconds", e);
        }
    }
    
    /**
     * Asynchronous send with callback
     * Demonstrates non-blocking pattern
     */
    public CompletableFuture<SendResult<String, Object>> sendMessageAsynchronous(
            String topic, KafkaTaskMessageDTO message) {
        
        logger.info("=== ASYNCHRONOUS SEND ===");
        logger.info("Sending message asynchronously to topic: {}", topic);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message)
                .thenApply(result -> result)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to send message asynchronously", throwable);
                    } else {
                        logger.info("Async send successful: {}", result.getRecordMetadata());
                        logProducerMetadata(result);
                    }
                });
        
        return future;
    }
    
    /**
     * Send with custom headers
     * Demonstrates adding metadata to messages
     */
    public void sendMessageWithHeaders(String topic, KafkaTaskMessageDTO message) {
        logger.info("=== SEND WITH CUSTOM HEADERS ===");
        
        // Training: Create producer record with custom headers
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, message);
        
        // Training: Add custom headers for tracing, routing, etc.
        record.headers().add(new RecordHeader("correlation-id", 
                UUID.randomUUID().toString().getBytes()));
        record.headers().add(new RecordHeader("message-version", "2.0".getBytes()));
        record.headers().add(new RecordHeader("source-service", 
                "task-management-service".getBytes()));
        record.headers().add(new RecordHeader("processing-priority", 
                getPriority(message).getBytes()));
        
        // Training: Add timestamp if not set
        if (record.timestamp() == null) {
            // This would be set automatically in newer Kafka clients
        }
        
        kafkaTemplate.send(record)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to send message with headers", throwable);
                } else {
                    logger.info("Message with headers sent successfully");
                    logger.info("Headers: {}", record.headers());
                }
            });
    }
    
    // ==================== ADVANCED PRODUCER PATTERNS ====================
    
    /**
     * Transactional producer example using Kafka's native transaction support
     * Demonstrates atomic operations across multiple messages
     * 
     * NOTE: This uses executeInTransaction() which handles transactions internally,
     * so no @Transactional annotation or KafkaTransactionManager bean is needed.
     */
    public void sendTransactionalMessages(String topic, KafkaTaskMessageDTO... messages) {
        logger.info("=== TRANSACTIONAL PRODUCER ===");
        logger.info("Starting Kafka transaction for {} messages", messages.length);
        
        // Training: Use executeInTransaction for native Kafka transaction support
        // This handles the transaction lifecycle automatically without needing
        // a separate KafkaTransactionManager bean
        kafkaTemplate.executeInTransaction(transactions -> {
            try {
                for (int i = 0; i < messages.length; i++) {
                    KafkaTaskMessageDTO message = messages[i];
                    logger.info("Sending transactional message {}/{}: {}", 
                               i + 1, messages.length, message.getEventType());
                    
                    // Send within the transaction
                    transactions.send(topic, message).get();
                    
                    // Training: Simulate business logic that might fail
                    if (shouldFailTransaction()) {
                        throw new RuntimeException("Simulated transaction failure");
                    }
                }
                
                logger.info("Transaction completed successfully");
                return null;
                
            } catch (Exception e) {
                logger.error("Transaction failed, all messages will be rolled back", e);
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Send with partition key
     * Demonstrates controlling message routing
     */
    public void sendWithPartitionKey(String topic, String partitionKey, KafkaTaskMessageDTO message) {
        logger.info("=== SEND WITH PARTITION KEY ===");
        logger.info("Using partition key: {} for topic: {}", partitionKey, topic);
        
        // Training: Send with explicit key for partitioning
        kafkaTemplate.send(topic, partitionKey, message)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to send with partition key", throwable);
                } else {
                    logger.info("Message sent to partition: {}", 
                               result.getRecordMetadata().partition());
                    logger.info("Partition key used: {}", partitionKey);
                }
            });
    }
    
    /**
     * Send with custom partitioner logic
     * Demonstrates advanced routing strategies
     */
    public void sendWithCustomPartitioning(String topic, KafkaTaskMessageDTO message) {
        logger.info("=== CUSTOM PARTITIONING ===");
        
        // Training: Calculate partition based on message content
        int partition = calculatePartition(message);
        String key = generatePartitionKey(message);
        
        ProducerRecord<String, Object> record = new ProducerRecord<>(
            topic, 
            partition, // Explicit partition
            key, 
            message
        );
        
        kafkaTemplate.send(record)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to send with custom partitioning", throwable);
                } else {
                    logger.info("Message sent to calculated partition: {}", partition);
                    logger.info("Using key: {} for consistent hashing", key);
                }
            });
    }
    
    // ==================== EVENT-DRIVEN COMMUNICATION PATTERNS ====================
    
    /**
     * Publish task created event
     * Demonstrates event-driven pattern for domain events
     */
    public void publishTaskCreatedEvent(Long taskId, String taskTitle, Long userId, String userName) {
        logger.info("=== PUBLISH TASK CREATED EVENT ===");
        
        KafkaTaskMessageDTO event = createTaskEvent(
            "TASK_CREATED", taskId, taskTitle, "TODO", userId, userName);
        
        // Training: Add domain-specific metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("domain", "task-management");
        metadata.put("eventVersion", "1.0");
        metadata.put("timestamp", System.currentTimeMillis());
        event.setAdditionalInfo(metadata.toString());
        
        sendDomainEvent("task-events", event, "task.created");
    }
    
    /**
     * Publish task updated event
     * Demonstrates state change events
     */
    public void publishTaskUpdatedEvent(Long taskId, String taskTitle, String taskStatus, 
                                       Long userId, String userName) {
        logger.info("=== PUBLISH TASK UPDATED EVENT ===");
        
        KafkaTaskMessageDTO event = createTaskEvent(
            "TASK_UPDATED", taskId, taskTitle, taskStatus, userId, userName);
        
        // Training: Include state transition information
        Map<String, Object> stateChange = new HashMap<>();
        stateChange.put("previousStatus", "TODO"); // Would come from DB in real app
        stateChange.put("newStatus", taskStatus);
        stateChange.put("changedBy", userName);
        event.setAdditionalInfo(stateChange.toString());
        
        sendDomainEvent("task-events", event, "task.updated");
    }
    
    /**
     * Publish task completed event
     * Demonstrates completion events with business context
     */
    public void publishTaskCompletedEvent(Long taskId, String taskTitle, 
                                         Long userId, String userName) {
        logger.info("=== PUBLISH TASK COMPLETED EVENT ===");
        
        KafkaTaskMessageDTO event = createTaskEvent(
            "TASK_COMPLETED", taskId, taskTitle, "DONE", userId, userName);
        
        // Training: Add completion metrics
        Map<String, Object> completionData = new HashMap<>();
        completionData.put("completionTime", System.currentTimeMillis());
        completionData.put("estimatedVsActual", "on-time"); // Simplified
        completionData.put("qualityScore", 95); // Example metric
        event.setAdditionalInfo(completionData.toString());
        
        sendDomainEvent("task-events", event, "task.completed");
    }
    
    /**
     * Send domain event with standardized headers
     */
    private void sendDomainEvent(String topic, KafkaTaskMessageDTO event, String eventType) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event);
        
        // Training: Standard event headers
        record.headers().add(new RecordHeader("event-id", 
                UUID.randomUUID().toString().getBytes()));
        record.headers().add(new RecordHeader("event-type", 
                eventType.getBytes()));
        record.headers().add(new RecordHeader("event-timestamp", 
                String.valueOf(System.currentTimeMillis()).getBytes()));
        record.headers().add(new RecordHeader("content-type", 
                "application/json".getBytes()));
        record.headers().add(new RecordHeader("schema-version", 
                "1.0".getBytes()));
        
        // Training: Send with guaranteed ordering for same partition key
        String partitionKey = event.getTaskId() != null ? 
                event.getTaskId().toString() : UUID.randomUUID().toString();
        
        kafkaTemplate.send(topic, partitionKey, record.value())
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to publish domain event: {}", eventType, throwable);
                    // Training: Implement retry logic or dead letter queue
                    handleFailedDomainEvent(event, throwable);
                } else {
                    logger.info("Domain event published: {} to partition {}", 
                               eventType, result.getRecordMetadata().partition());
                }
            });
    }
    
    // ==================== ERROR HANDLING AND RETRIES ====================
    
    /**
     * Send with retry logic
     * Demonstrates producer-side retry patterns
     */
    public void sendWithRetry(String topic, KafkaTaskMessageDTO message, int maxRetries) {
        logger.info("=== SEND WITH RETRY LOGIC ===");
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Send attempt {}/{}", attempt, maxRetries);
                
                SendResult<String, Object> result = kafkaTemplate.send(topic, message)
                        .get(5, TimeUnit.SECONDS); // Shorter timeout for retries
                
                logger.info("Send successful on attempt {}", attempt);
                return;
                
            } catch (Exception e) {
                logger.warn("Send attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt == maxRetries) {
                    logger.error("Max retries ({}) exceeded for message: {}", 
                                maxRetries, message.getMessageId());
                    handlePermanentFailure(message, e);
                    return;
                }
                
                // Training: Exponential backoff
                try {
                    long backoffTime = (long) (Math.pow(2, attempt - 1) * 100); // 100ms, 200ms, 400ms...
                    Thread.sleep(backoffTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry backoff", ie);
                }
            }
        }
    }
    
    /**
     * Handle permanent send failure
     * Demonstrates failure handling strategies
     */
    private void handlePermanentFailure(KafkaTaskMessageDTO message, Exception cause) {
        logger.error("Permanent failure for message: {}", message.getMessageId(), cause);
        
        // Training: Failure handling strategies
        // 1. Log for manual intervention
        logger.error("Message permanently failed: {}", message);
        
        // 2. Store in local database for later retry
        storeFailedMessage(message, cause);
        
        // 3. Send alert to monitoring system
        sendFailureAlert(message, cause);
        
        // 4. Optionally throw exception to caller
        throw new RuntimeException("Failed to send message after all retries", cause);
    }
    
    /**
     * Handle failed domain event
     */
    private void handleFailedDomainEvent(KafkaTaskMessageDTO event, Throwable throwable) {
        logger.error("Domain event failed: {}", event.getEventType(), throwable);
        
        // Training: Domain-specific failure handling
        // Could involve:
        // - Compensating transactions
        // - Saga pattern rollback
        // - Notification to stakeholders
        // - Audit logging
        
        logger.warn("Domain event failure requires manual intervention: {}", 
                   event.getEventType());
    }
    
    // ==================== HELPER METHODS ====================
    
    private KafkaTaskMessageDTO createTaskEvent(String eventType, Long taskId, 
                                               String taskTitle, String taskStatus,
                                               Long userId, String userName) {
        return new KafkaTaskMessageDTO(
            eventType, taskId, taskTitle, taskStatus, userId, userName
        );
    }
    
    private String getPriority(KafkaTaskMessageDTO message) {
        // Training: Simple priority calculation
        if ("TASK_CREATED".equals(message.getEventType())) {
            return "HIGH";
        } else if ("TASK_COMPLETED".equals(message.getEventType())) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
    
    private int calculatePartition(KafkaTaskMessageDTO message) {
        // Training: Simple partition calculation based on task ID
        if (message.getTaskId() == null) {
            return 0;
        }
        return Math.abs(message.getTaskId().hashCode()) % 4; // Assume 4 partitions
    }
    
    private String generatePartitionKey(KafkaTaskMessageDTO message) {
        // Training: Generate key for consistent partitioning
        if (message.getTaskId() != null) {
            return "task-" + message.getTaskId();
        } else if (message.getUserId() != null) {
            return "user-" + message.getUserId();
        } else {
            return UUID.randomUUID().toString();
        }
    }
    
    private boolean shouldFailTransaction() {
        // Training: Simulate random transaction failure (10% chance)
        return Math.random() < 0.1;
    }
    
    private void logProducerMetadata(SendResult<String, Object> result) {
        logger.info("Producer Metadata:");
        logger.info("  Topic: {}", result.getRecordMetadata().topic());
        logger.info("  Partition: {}", result.getRecordMetadata().partition());
        logger.info("  Offset: {}", result.getRecordMetadata().offset());
        logger.info("  Timestamp: {}", result.getRecordMetadata().timestamp());
        logger.info("  Serialized Key Size: {}", result.getRecordMetadata().serializedKeySize());
        logger.info("  Serialized Value Size: {}", result.getRecordMetadata().serializedValueSize());
    }
    
    // Training: Stub methods for demonstration
    private void storeFailedMessage(KafkaTaskMessageDTO message, Exception cause) {
        logger.info("Storing failed message in local database for later retry");
        // In real implementation, would insert into failed_messages table
    }
    
    private void sendFailureAlert(KafkaTaskMessageDTO message, Exception cause) {
        logger.info("Sending alert to monitoring system");
        // In real implementation, would send to monitoring/alerting system
    }
    
    // ==================== CHAINED TRANSACTION MANAGER (DB + KAFKA) ====================
    /**
     * Simplified example that can run without DB entities
     * Demonstrates the concept without requiring TaskRepository injection
     */
    @Transactional(value = "chainedTransactionManager", rollbackFor = Exception.class)
    public void processOrderWithChainedTransaction(Long orderId, String orderDetails) {
        logger.info("=== CHAINED TRANSACTION (DB + KAFKA) ===");
        logger.info("Starting chained transaction for order: {}", orderId);
        
        try {
            // Simulate DB operation - in real app, use repository
            logger.info("1. DB Operation: Saving order {} to database", orderId);
            // orderRepo.save(order);  // Would be here in real implementation
            
            // Simulate Kafka operation
            logger.info("2. Kafka Operation: Sending event for order {}", orderId);
            KafkaTaskMessageDTO event = new KafkaTaskMessageDTO(
                "ORDER_CREATED",
                orderId,
                orderDetails,
                "PENDING",
                1L,
                "system"
            );
            
            // Send within the same transaction
            kafkaTemplate.send("orders", orderId.toString(), event).get(10, TimeUnit.SECONDS);
            logger.info("Kafka message sent for order: {}", orderId);
            
            logger.info("Chained transaction completed successfully - both DB and Kafka will commit");
            
        } catch (Exception e) {
            logger.error("Chained transaction failed - both DB and Kafka will be rolled back", e);
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }
    
    // ==================== @TRANSACTIONAL EVENT LISTENER (BETTER APPROACH!) ====================
    
    /**
     * 🎯 BETTER APPROACH: @TransactionalEventListener
     * 
     * This is the RECOMMENDED approach for DB + Kafka transactions!
     * 
     * HOW IT WORKS:
     * 1. Your service method saves to DB and publishes a Spring event
     * 2. DB transaction commits successfully
     * 3. ONLY THEN @TransactionalEventListener fires and sends to Kafka
     * 4. If DB fails, NO Kafka message is sent (no inconsistency!)
     * 
     * ADVANTAGES over ChainedKafkaTransactionManager:
     * ✅ Cleaner separation of concerns
     * ✅ Kafka failure doesn't affect DB transaction
     * ✅ No dual-write problem
     * ✅ Easier to test and maintain
     * ✅ Recommended for production systems
     */
    
    /**
     * Example: Using @TransactionalEventListener for reliable DB + Kafka
     * 
     * Step 1: In your service/repository, save to DB and publish event:
     * 
     * @Transactional
     * public void createTask(Task task) {
     *     taskRepository.save(task);
     *     applicationEventPublisher.publishEvent(new TaskCreatedEvent(task));
     * }
     * 
     * Step 2: This listener automatically fires AFTER commit:
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreatedEvent(TaskCreatedEventForKafka event) {
        logger.info("=== @TRANSACTIONAL EVENT LISTENER ===");
        logger.info("DB transaction committed! Now sending Kafka message for: {}", event.getTaskId());
        
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TASK_CREATED",
            event.getTaskId(),
            event.getTaskTitle(),
            event.getTaskStatus(),
            event.getUserId(),
            event.getUserName()
        );
        
        try {
            kafkaTemplate.send("task-events", event.getTaskId().toString(), message).get(10, TimeUnit.SECONDS);
            logger.info("✅ Kafka message sent successfully after DB commit!");
        } catch (Exception e) {
            logger.error("❌ Kafka send failed after DB commit - DB is safe!", e);
            // Kafka failure doesn't affect DB - you can retry separately
            // This is the key advantage!
        }
    }
    
    /**
     * Simple event class for demonstration
     * In real app, this would be your domain event
     */
    public static class TaskCreatedEventForKafka {
        private final Long taskId;
        private final String taskTitle;
        private final String taskStatus;
        private final Long userId;
        private final String userName;
        
        public TaskCreatedEventForKafka(Long taskId, String taskTitle, String taskStatus, 
                                        Long userId, String userName) {
            this.taskId = taskId;
            this.taskTitle = taskTitle;
            this.taskStatus = taskStatus;
            this.userId = userId;
            this.userName = userName;
        }
        
        public Long getTaskId() { return taskId; }
        public String getTaskTitle() { return taskTitle; }
        public String getTaskStatus() { return taskStatus; }
        public Long getUserId() { return userId; }
        public String getUserName() { return userName; }
    }
    
    /**
     * Example usage method showing how to use @TransactionalEventListener approach
     * 
     * To use this, inject ApplicationEventPublisher in your service:
     * 
     * @Transactional
     * public void createTaskWithKafkaEvent(Task task) {
     *     // Step 1: Save to DB (normal @Transactional)
     *     Task saved = taskRepository.save(task);
     *     
     *     // Step 2: Publish event (NOT sent to Kafka yet!)
     *     eventPublisher.publishEvent(new TaskCreatedEventForKafka(
     *         saved.getId(),
     *         saved.getTitle(),
     *         saved.getStatus(),
     *         saved.getAssignee().getId(),
     *         saved.getAssignee().getUsername()
     *     ));
     *     
     *     // Step 3: When method returns, DB transaction commits
     *     // Step 4: @TransactionalEventListener fires AFTER commit, sends to Kafka
     * }
     */
    public void demonstrateTransactionalEventListener() {
        logger.info("=== DEMONSTRATING @TransactionalEventListener APPROACH ===");
        logger.info("This approach is BETTER than ChainedKafkaTransactionManager because:");
        logger.info("1. Kafka message is sent ONLY after DB commit succeeds");
        logger.info("2. If Kafka fails, DB is NOT affected");
        logger.info("3. Cleaner separation of concerns");
        logger.info("4. Recommended for production systems!");
    }
    
    // ==================== OUTBOX PATTERN (MOST RELIABLE!) ====================
    
    /**
     * 🎯 MOST RELIABLE: Outbox Pattern
     * 
     * This is the MOST RELIABLE approach for DB + Kafka!
     * 
     * HOW IT WORKS:
     * 1. Save to DB + Save to outbox table in SAME transaction
     * 2. Transaction commits - both DB and outbox are persisted
     * 3. Scheduler polls outbox and publishes to Kafka
     * 4. Mark as published after successful Kafka send
     * 
     * ADVANTAGES:
     * ✅ GUARANTEED consistency - DB and Kafka ALWAYS in sync
     * ✅ If Kafka fails, message stays in outbox for retry
     * ✅ No dual-write problem
     * ✅ Best for critical systems (banking, payments, etc.)
     * 
     * COMPONENTS NEEDED:
     * - OutboxMessage entity (see entity/OutboxMessage.java)
     * - OutboxRepository (see repository/OutboxRepository.java)
     * - OutboxScheduler (see service/OutboxScheduler.java)
     */
    
    // To use the Outbox Pattern, inject these in your service:
    // private final OutboxRepository outboxRepository;
    // private final ObjectMapper objectMapper;
  
     /* 
     * 
     * Step 4: OutboxScheduler automatically:
     * - Reads unpublished messages from outbox
     * - Publishes to Kafka
     * - Marks as published
     */


    @Transactional
    public void createTaskWithOutbox(Task task, Long userId, String userName) {
        logger.info("=== OUTBOX PATTERN: Creating task with outbox ===");
        
        // 1. Save main entity to DB
        Task saved = taskRepository.save(task);
        logger.info("1. Saved task to database: id={}, title={}", saved.getId(), saved.getTitle());

        // 2. Create Kafka event payload
        KafkaTaskMessageDTO event = new KafkaTaskMessageDTO(
            "TASK_CREATED",
            saved.getId(),
            saved.getTitle(),
            saved.getStatus() != null ? saved.getStatus().name() : "TODO",
            userId,
            userName
        );

        // 3. Save to outbox table (SAME transaction!)
        try {
            OutboxMessage outbox = new OutboxMessage(
                "Task",
                saved.getId().toString(),
                "TASK_CREATED",
                objectMapper.writeValueAsString(event)
            );
            outboxRepository.save(outbox);
            logger.info("2. Saved to outbox table: aggregateId={}, eventType=TASK_CREATED", saved.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }

        // Transaction commits - both task and outbox are saved!
        // OutboxScheduler will publish to Kafka asynchronously
        logger.info("✅ Transaction committed! OutboxScheduler will publish to Kafka...");
    }
    
    /**
     * Demonstrate Outbox Pattern
     */
    public void demonstrateOutboxPattern() {
        logger.info("=== DEMONSTRATING OUTBOX PATTERN ===");
        logger.info("This is the MOST RELIABLE approach for DB + Kafka:");
        logger.info("1. Write to DB + Write to outbox in SAME transaction");
        logger.info("2. Transaction commits - both persisted");
        logger.info("3. OutboxScheduler polls and publishes to Kafka");
        logger.info("4. If Kafka fails, message stays in outbox for retry");
        logger.info("✅ GUARANTEED consistency - Best for critical systems!");
    }
    
    /**
     * Compare all three approaches
     */
    public void compareAllApproaches() {
        logger.info("=== COMPARING ALL THREE APPROACHES ===");
        
        logger.info("");
        logger.info("1. ChainedKafkaTransactionManager:");
        logger.info("   ✅ Both operations in same transaction");
        logger.info("   ❌ If Kafka fails after DB commit, can have issues");
        logger.info("   ⚠️  Complex rollback scenarios");
        
        logger.info("");
        logger.info("2. @TransactionalEventListener:");
        logger.info("   ✅ Kafka ONLY after DB commit succeeds");
        logger.info("   ✅ Kafka failure doesn't affect DB");
        logger.info("   ✅ Cleaner separation of concerns");
        logger.info("   ⚠️  Requires async Kafka send");
        
        logger.info("");
        logger.info("3. Outbox Pattern (MOST RELIABLE):");
        logger.info("   ✅ GUARANTEED consistency");
        logger.info("   ✅ Kafka failure doesn't affect DB");
        logger.info("   ✅ Automatic retry via scheduler");
        logger.info("   ✅ Best for critical systems");
        logger.info("   ⚠️  Requires additional outbox table");
        
        logger.info("");
        logger.info("RECOMMENDATION:");
        logger.info("- Simple cases: @TransactionalEventListener");
        logger.info("- Critical systems: Outbox Pattern");
    }
}
