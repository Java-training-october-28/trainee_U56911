package com.example.demo.service;

import com.example.demo.dto.KafkaTaskMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Enhanced Kafka Consumer Service for Training Purposes
 * Demonstrates:
 * 1. Async Kafka listener implementation
 * 2. Manual acknowledgment
 * 3. Dead Letter Topic handling
 * 4. Batch processing
 * 5. Error handling and retries
 */
@Service
public class EnhancedKafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedKafkaConsumerService.class);
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(5);

    // In-memory store of received messages (last 500)
    private final CopyOnWriteArrayList<KafkaTaskMessageDTO> receivedMessages = new CopyOnWriteArrayList<>();
    private static final int MAX_STORED_MESSAGES = 500;

    public List<KafkaTaskMessageDTO> searchMessages(String keyword) {
        String lower = keyword == null ? "" : keyword.toLowerCase();
        return receivedMessages.stream()
            .filter(m -> lower.isEmpty()
                || (m.getTaskTitle() != null && m.getTaskTitle().toLowerCase().contains(lower))
                || (m.getEventType() != null && m.getEventType().toLowerCase().contains(lower))
                || (m.getUserName() != null && m.getUserName().toLowerCase().contains(lower))
                || (m.getMessageId() != null && m.getMessageId().toString().toLowerCase().contains(lower)))
            .collect(Collectors.toList());
    }

    public List<KafkaTaskMessageDTO> getAllMessages() {
        return Collections.unmodifiableList(new ArrayList<>(receivedMessages));
    }

    private void storeMessage(KafkaTaskMessageDTO message) {
        if (receivedMessages.size() >= MAX_STORED_MESSAGES) {
            receivedMessages.remove(0);
        }
        receivedMessages.add(message);
    }
    
    // ==================== RETRYABLE TOPIC LISTENER (Annotation-based DLQ) ====================
    
    /**
     * Kafka Listener with @RetryableTopic annotation
     * DLQ handling moved from config to annotation
     * 
     * Retry topics created: task-events-0, task-events-1, task-events-2
     * DLT topic: task-events.DLQ
     */
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        dltTopicSuffix = ".DLQ",
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        include = {RuntimeException.class, Exception.class}
    )
    @KafkaListener(
        topics = "${spring.kafka.topic.task-events:task-events}",
        groupId = "${spring.kafka.consumer.group-id:task-management-training-group}",
        containerFactory = "enhancedKafkaListenerContainerFactory"
    )
    public void consumeTaskEvent(
            @Payload KafkaTaskMessageDTO message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment acknowledgment) {
        
        logger.info("=== RETRYABLE TOPIC LISTENER ===");
        logger.info("Received message from topic: {}, partition: {}, key: {}", topic, partition, key);
        logger.info("Message timestamp: {}, payload: {}", timestamp, message);
        
        try {
            // Store message for search
            storeMessage(message);
            // Training: Process message with business logic
            processMessageWithRetryLogic(message);
            
            // Training: Manual acknowledgment after successful processing
            acknowledgment.acknowledge();
            logger.info("Successfully processed and acknowledged message for task: {}", message.getTaskId());
            
        } catch (Exception e) {
            logger.error("Failed to process message: {}", message, e);
            // Throw exception to trigger retry mechanism
            throw e;
        }
    }
    
    /**
     * Dead Letter Topic Handler
     * Called when all retries are exhausted
     */
    @DltHandler
    public void handleDlt(
            @Payload KafkaTaskMessageDTO message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {
        
        logger.info("=== DEAD LETTER TOPIC HANDLER (@DltHandler) ===");
        logger.info("Received failed message in DLT from topic: {}", topic);
        logger.info("Original message: {}", message);
        logger.info("Failure reason: {}", exceptionMessage);
        
        // Training: Handle DLT message - log, alert, store for manual review
        handleDeadLetterMessage(message, exceptionMessage);
    }
    
    // ==================== ASYNC KAFKA LISTENER ====================
    
    /**
     * Async Kafka Listener using CompletableFuture
     * Demonstrates non-blocking message processing
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.task-events:task-events}",
        groupId = "${spring.kafka.consumer.async-group-id:task-management-async-group}",
        containerFactory = "asyncKafkaListenerContainerFactory"
    )
    public void consumeTaskEventAsync(
            @Payload KafkaTaskMessageDTO message,
            Acknowledgment acknowledgment) {
        
        logger.info("=== ASYNC LISTENER ===");
        logger.info("Starting async processing for message: {}", message.getMessageId());
        
        CompletableFuture.runAsync(() -> {
            try {
                // Training: Simulate async processing
                simulateAsyncProcessing(message);
                
                // Training: Acknowledge after async processing
                acknowledgment.acknowledge();
                logger.info("Async processing completed for message: {}", message.getMessageId());
                
            } catch (Exception e) {
                logger.error("Async processing failed for message: {}", message.getMessageId(), e);
                // Training: In async context, we need to handle errors differently
                // Could use a separate error queue or retry mechanism
            }
        }, asyncExecutor);
    }
    
    // ==================== BATCH PROCESSING LISTENER ====================
    
    /**
     * Batch Kafka Listener for processing multiple messages at once
     * Demonstrates batch processing efficiency
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.task-events:task-events}",
        groupId = "${spring.kafka.consumer.batch-group-id:task-management-batch-group}",
        containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consumeTaskEventsBatch(List<ConsumerRecord<String, KafkaTaskMessageDTO>> records) {
        
        logger.info("=== BATCH PROCESSING ===");
        logger.info("Received batch of {} messages", records.size());
        
        // Training: Process batch efficiently
        for (ConsumerRecord<String, KafkaTaskMessageDTO> record : records) {
            try {
                processBatchMessage(record.value(), record);
            } catch (Exception e) {
                logger.error("Failed to process message in batch: {}", record.value(), e);
                // Training: In batch processing, you might want to:
                // 1. Log the error but continue with other messages
                // 2. Send failed messages to a separate queue
                // 3. Implement circuit breaker pattern
            }
        }
        
        logger.info("Batch processing completed for {} messages", records.size());
    }
    
    // ==================== DEAD LETTER TOPIC LISTENER ====================
    
    /**
     * Dead Letter Topic Listener
     * Demonstrates handling of failed messages
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.task-events:task-events}.DLQ",
        groupId = "${spring.kafka.consumer.dlq-group-id:task-management-dlq-group}"
    )
    public void consumeDeadLetterMessage(
            @Payload KafkaTaskMessageDTO message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stackTrace) {
        
        logger.info("=== DEAD LETTER TOPIC HANDLER ===");
        logger.info("Received failed message from DLQ: {}", topic);
        logger.info("Original message: {}", message);
        logger.info("Failure reason: {}", exceptionMessage);
        
        if (stackTrace != null) {
            logger.debug("Stack trace: {}", stackTrace);
        }
        
        // Training: Handle DLQ messages
        handleDeadLetterMessage(message, exceptionMessage);
    }
    
    // ==================== PROCESSING METHODS ====================
    
    /**
     * Process message with retry logic simulation
     * Demonstrates resilience patterns
     */
    private void processMessageWithRetryLogic(KafkaTaskMessageDTO message) {
        logger.info("Processing message with retry logic: {}", message.getEventType());
        
        // Training: Simulate different processing scenarios
        switch (message.getEventType()) {
            case "TASK_CREATED":
                processTaskCreatedWithRetry(message);
                break;
            case "TASK_UPDATED":
                processTaskUpdatedWithCircuitBreaker(message);
                break;
            case "TASK_COMPLETED":
                processTaskCompletedWithFallback(message);
                break;
            case "TEST_EVENT":
                // Training: Simulate random failure for demonstration
                if (Math.random() < 0.3) {
                    throw new RuntimeException("Simulated processing failure for training");
                }
                logger.info("Test event processed successfully");
                break;
            default:
                logger.warn("Unknown event type: {}", message.getEventType());
        }
    }
    
    /**
     * Process task created with simulated retry logic
     */
    private void processTaskCreatedWithRetry(KafkaTaskMessageDTO message) {
        int maxRetries = 3;
        int attempt = 0;
        
        while (attempt < maxRetries) {
            try {
                attempt++;
                logger.info("Processing TASK_CREATED (attempt {}/{}): {}", 
                           attempt, maxRetries, message.getTaskTitle());
                
                // Training: Simulate external service call
                simulateExternalServiceCall();
                
                logger.info("Task '{}' created successfully", message.getTaskTitle());
                return;
                
            } catch (Exception e) {
                logger.warn("Attempt {} failed for task '{}': {}", 
                           attempt, message.getTaskTitle(), e.getMessage());
                
                if (attempt == maxRetries) {
                    throw new RuntimeException("Max retries exceeded for task: " + message.getTaskTitle(), e);
                }
                
                // Training: Exponential backoff
                try {
                    Thread.sleep(100 * attempt); // 100ms, 200ms, 300ms
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
    }
    
    /**
     * Process task updated with circuit breaker simulation
     */
    private void processTaskUpdatedWithCircuitBreaker(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_UPDATED with circuit breaker pattern");
        
        // Training: Simulate circuit breaker states
        String circuitState = getCircuitState();
        
        switch (circuitState) {
            case "CLOSED":
                logger.info("Circuit CLOSED - Processing normally");
                // Normal processing
                break;
            case "OPEN":
                logger.warn("Circuit OPEN - Using fallback");
                useFallbackStrategy(message);
                break;
            case "HALF_OPEN":
                logger.info("Circuit HALF_OPEN - Testing recovery");
                testCircuitRecovery(message);
                break;
        }
    }
    
    /**
     * Process task completed with fallback strategy
     */
    private void processTaskCompletedWithFallback(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_COMPLETED with fallback strategy");
        
        try {
            // Primary processing strategy
            primaryProcessingStrategy(message);
        } catch (Exception e) {
            logger.warn("Primary strategy failed, using fallback: {}", e.getMessage());
            
            // Fallback strategy
            fallbackProcessingStrategy(message);
        }
    }
    
    /**
     * Simulate async processing
     */
    private void simulateAsyncProcessing(KafkaTaskMessageDTO message) throws InterruptedException {
        logger.info("Starting async processing for: {}", message.getEventType());
        
        // Training: Simulate processing time
        Thread.sleep(1000); // 1 second processing
        
        logger.info("Async processing completed for: {}", message.getEventType());
    }
    
    /**
     * Process batch message
     */
    private void processBatchMessage(KafkaTaskMessageDTO message, ConsumerRecord<String, KafkaTaskMessageDTO> record) {
        // Training: Efficient batch processing
        logger.debug("Processing batch message: {} from partition {}", 
                    message.getMessageId(), record.partition());
        
        // Simple processing for demonstration
        if ("TASK_CREATED".equals(message.getEventType())) {
            logger.info("Batch: Task created - {}", message.getTaskTitle());
        }
    }
    
    /**
     * Handle dead letter message
     */
    private void handleDeadLetterMessage(KafkaTaskMessageDTO message, String failureReason) {
        logger.warn("Handling dead letter message for task: {}", message.getTaskId());
        logger.warn("Failure reason: {}", failureReason);
        
        // Store the failed message for manual review (in-memory for demo)
        logger.info("DLQ message archived for manual review - Task ID: {}, Event: {}", 
            message.getTaskId(), message.getEventType());
    }

    // ==================== HELPER METHODS ====================
    
    private void simulateExternalServiceCall() {
        // Training: Simulate external service call that might fail
        if (Math.random() < 0.2) {
            throw new RuntimeException("External service unavailable");
        }
    }
    
    private String getCircuitState() {
        // Training: Simulate circuit breaker state
        double random = Math.random();
        if (random < 0.7) return "CLOSED";
        if (random < 0.9) return "OPEN";
        return "HALF_OPEN";
    }
    
    private void useFallbackStrategy(KafkaTaskMessageDTO message) {
        logger.info("Using fallback strategy for task: {}", message.getTaskTitle());
        // Training: Fallback logic
        // - Cache previous state
        // - Use default values
        // - Queue for later processing
    }
    
    private void testCircuitRecovery(KafkaTaskMessageDTO message) {
        logger.info("Testing circuit recovery for task: {}", message.getTaskTitle());
        // Training: Test if service is recovering
    }
    
    private void primaryProcessingStrategy(KafkaTaskMessageDTO message) {
        // Training: Simulate primary strategy failure
        if (Math.random() < 0.4) {
            throw new RuntimeException("Primary processing failed");
        }
        logger.info("Primary strategy successful for task: {}", message.getTaskTitle());
    }
    
    private void fallbackProcessingStrategy(KafkaTaskMessageDTO message) {
        logger.info("Fallback strategy executed for task: {}", message.getTaskTitle());
        // Training: Simple fallback logic
    }
    
    /**
     * Cleanup resources
     */
    public void shutdown() {
        asyncExecutor.shutdown();
        logger.info("EnhancedKafkaConsumerService shutdown completed");
    }
}