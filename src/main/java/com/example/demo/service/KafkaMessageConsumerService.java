package com.example.demo.service;

import com.example.demo.dto.KafkaTaskMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Service for consuming Kafka messages using Spring Cloud Stream Function
 */
@Service
public class KafkaMessageConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumerService.class);
    
    /**
     * Consumer function for processing task events from Kafka
     * This method will be automatically bound to the 'taskInput' channel
     * based on the configuration in application.yaml
     */
    @Bean
    public Consumer<KafkaTaskMessageDTO> taskInput() {
        return message -> {
            logger.info("Received Kafka message: {}", message);
            processTaskEvent(message);
        };
    }
    
    /**
     * Process different types of task events
     */
    private void processTaskEvent(KafkaTaskMessageDTO message) {
        try {
            switch (message.getEventType()) {
                case "TASK_CREATED":
                    handleTaskCreated(message);
                    break;
                case "TASK_UPDATED":
                    handleTaskUpdated(message);
                    break;
                case "TASK_COMPLETED":
                    handleTaskCompleted(message);
                    break;
                case "TASK_DELETED":
                    handleTaskDeleted(message);
                    break;
                case "TEST_EVENT":
                    handleTestEvent(message);
                    break;
                default:
                    logger.warn("Unknown event type received: {}", message.getEventType());
                    handleUnknownEvent(message);
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", message, e);
        }
    }
    
    /**
     * Handle task created events
     */
    private void handleTaskCreated(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_CREATED event for task: {} (ID: {})", 
                   message.getTaskTitle(), message.getTaskId());
        // In a real application, you might:
        // 1. Send notifications to team members
        // 2. Update analytics
        // 3. Trigger downstream processes
        // 4. Log to audit trail
        logger.info("Task '{}' created by user '{}'", 
                   message.getTaskTitle(), message.getUserName());
    }
    
    /**
     * Handle task updated events
     */
    private void handleTaskUpdated(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_UPDATED event for task: {} (ID: {})", 
                   message.getTaskTitle(), message.getTaskId());
        logger.info("Task '{}' status changed to '{}'", 
                   message.getTaskTitle(), message.getTaskStatus());
        
        // Example: Send notification if task is now blocked
        if ("BLOCKED".equals(message.getTaskStatus())) {
            logger.info("Sending notification: Task '{}' is blocked and needs attention", 
                       message.getTaskTitle());
        }
    }
    
    /**
     * Handle task completed events
     */
    private void handleTaskCompleted(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_COMPLETED event for task: {} (ID: {})", 
                   message.getTaskTitle(), message.getTaskId());
        logger.info("Task '{}' completed by user '{}' - Congratulations!", 
                   message.getTaskTitle(), message.getUserName());
        
        // Example: Update project completion metrics
        logger.info("Updating project metrics for completed task");
    }
    
    /**
     * Handle task deleted events
     */
    private void handleTaskDeleted(KafkaTaskMessageDTO message) {
        logger.info("Processing TASK_DELETED event for task: {} (ID: {})", 
                   message.getTaskTitle(), message.getTaskId());
        logger.info("Task '{}' deleted by user '{}' - Archiving related data", 
                   message.getTaskTitle(), message.getUserName());
        
        // Example: Archive task data for compliance
        logger.info("Archiving task data for compliance purposes");
    }
    
    /**
     * Handle test events
     */
    private void handleTestEvent(KafkaTaskMessageDTO message) {
        logger.info("Processing TEST_EVENT: {}", message.getAdditionalInfo());
        logger.info("Test message received successfully! Kafka integration is working.");
    }
    
    /**
     * Handle unknown events
     */
    private void handleUnknownEvent(KafkaTaskMessageDTO message) {
        logger.info("Received unknown event type '{}' for task ID: {}", 
                   message.getEventType(), message.getTaskId());
        logger.info("Full message details: {}", message);
    }
    
    /**
     * Manual consumer method (alternative approach using @StreamListener)
     * This is kept as an example of alternative approach
     */
    /*
    @StreamListener("taskInput")
    public void handleTaskMessage(KafkaTaskMessageDTO message) {
        logger.info("Received message via @StreamListener: {}", message);
        processTaskEvent(message);
    }
    */
}
