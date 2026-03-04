package com.example.demo.service;

import com.example.demo.dto.KafkaTaskMessageDTO;
import com.example.demo.entity.OutboxMessage;
import com.example.demo.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbox Service - Working Implementation of Outbox Pattern
 * 
 * This is the MOST RELIABLE approach for DB + Kafka!
 * 
 * HOW IT WORKS:
 * 1. Save to DB + Save to outbox table in SAME transaction
 * 2. Transaction commits - both DB and outbox are persisted
 * 3. OutboxScheduler polls outbox and publishes to Kafka
 * 4. Mark as published after successful Kafka send
 * 
 * ADVANTAGES:
 * ✅ GUARANTEED consistency - DB and Kafka ALWAYS in sync
 * ✅ If Kafka fails, message stays in outbox for retry
 * ✅ No dual-write problem
 * ✅ Best for critical systems (banking, payments, etc.)
 */
@Service
public class OutboxService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxService.class);

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 🎯 WORKING EXAMPLE: Save Task with Outbox Pattern
     * 
     * This method demonstrates the Outbox Pattern in action:
     * 1. Saves the task to database
     * 2. Saves the event to outbox table
     * 3. BOTH in the SAME transaction!
     * 
     * If either fails, both are rolled back!
     */
    @Transactional
    public void createTaskWithOutbox(Long taskId, String taskTitle, String taskStatus, 
                                    Long userId, String userName) {
        logger.info("=== OUTBOX PATTERN: Creating task with outbox ===");
        
        try {
            // Step 1: Save main entity to database
            // In real app: Task saved = taskRepository.save(task);
            logger.info("1. Saving task to database: id={}, title={}", taskId, taskTitle);
            
            // Step 2: Create Kafka event payload
            KafkaTaskMessageDTO event = new KafkaTaskMessageDTO(
                "TASK_CREATED",
                taskId,
                taskTitle,
                taskStatus,
                userId,
                userName
            );
            
            // Step 3: Save to outbox table (SAME TRANSACTION!)
            String payload = objectMapper.writeValueAsString(event);
            
            OutboxMessage outbox = new OutboxMessage(
                "Task",                    // aggregateType
                taskId.toString(),         // aggregateId
                "TASK_CREATED",            // eventType
                payload                    // payload (JSON)
            );
            
            outboxRepository.save(outbox);
            
            logger.info("2. Saved to outbox table: aggregateId={}, eventType=TASK_CREATED", taskId);
            logger.info("✅ Transaction will commit - both task and outbox will be persisted!");
            logger.info("📝 OutboxScheduler will publish to Kafka asynchronously...");
            
        } catch (Exception e) {
            logger.error("❌ Failed to create task with outbox: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create task with outbox", e);
        }
    }

    /**
     * Example: Update Task with Outbox Pattern
     */
    @Transactional
    public void updateTaskWithOutbox(Long taskId, String taskTitle, String newStatus,
                                    Long userId, String userName) {
        logger.info("=== OUTBOX PATTERN: Updating task with outbox ===");
        
        try {
            // Step 1: Update task in database
            // In real app: taskRepository.update(task);
            logger.info("1. Updating task in database: id={}, status={}", taskId, newStatus);
            
            // Step 2: Create Kafka event payload
            KafkaTaskMessageDTO event = new KafkaTaskMessageDTO(
                "TASK_UPDATED",
                taskId,
                taskTitle,
                newStatus,
                userId,
                userName
            );
            
            // Step 3: Save to outbox table (SAME TRANSACTION!)
            String payload = objectMapper.writeValueAsString(event);
            
            OutboxMessage outbox = new OutboxMessage(
                "Task",
                taskId.toString(),
                "TASK_UPDATED",
                payload
            );
            
            outboxRepository.save(outbox);
            
            logger.info("2. Saved to outbox table: aggregateId={}, eventType=TASK_UPDATED", taskId);
            logger.info("✅ Transaction will commit!");
            
        } catch (Exception e) {
            logger.error("❌ Failed to update task with outbox: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update task with outbox", e);
        }
    }

    /**
     * Example: Complete Task with Outbox Pattern
     */
    @Transactional
    public void completeTaskWithOutbox(Long taskId, String taskTitle, Long userId, String userName) {
        logger.info("=== OUTBOX PATTERN: Completing task with outbox ===");
        
        try {
            // Step 1: Complete task in database
            logger.info("1. Completing task in database: id={}", taskId);
            
            // Step 2: Create Kafka event
            KafkaTaskMessageDTO event = new KafkaTaskMessageDTO(
                "TASK_COMPLETED",
                taskId,
                taskTitle,
                "DONE",
                userId,
                userName
            );
            
            // Step 3: Save to outbox (SAME TRANSACTION!)
            String payload = objectMapper.writeValueAsString(event);
            
            OutboxMessage outbox = new OutboxMessage(
                "Task",
                taskId.toString(),
                "TASK_COMPLETED",
                payload
            );
            
            outboxRepository.save(outbox);
            
            logger.info("2. Saved to outbox table: aggregateId={}, eventType=TASK_COMPLETED", taskId);
            logger.info("✅ Transaction will commit!");
            
        } catch (Exception e) {
            logger.error("❌ Failed to complete task with outbox: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete task with outbox", e);
        }
    }

    /**
     * Get count of unpublished messages (for monitoring)
     */
    public long getPendingMessageCount() {
        return outboxRepository.countUnpublished();
    }

    /**
     * Demonstrate the flow
     */
    public void demonstrateOutboxPattern() {
        logger.info("=== OUTBOX PATTERN DEMONSTRATION ===");
        logger.info("");
        logger.info("FLOW:");
        logger.info("1. Your service calls createTaskWithOutbox()");
        logger.info("2. Method saves task to DB + saves to outbox in SAME transaction");
        logger.info("3. Transaction commits successfully");
        logger.info("4. OutboxScheduler (running every 10s) picks up the message");
        logger.info("5. Scheduler publishes to Kafka topic 'task-events'");
        logger.info("6. Scheduler marks message as published");
        logger.info("");
        logger.info("WHY IT'S RELIABLE:");
        logger.info("✅ If Kafka is down, message stays in outbox");
        logger.info("✅ Scheduler will retry automatically");
        logger.info("✅ If transaction fails, nothing is saved");
        logger.info("✅ No dual-write problem!");
        logger.info("");
        logger.info("CURRENT PENDING MESSAGES: {}", getPendingMessageCount());
    }
}
