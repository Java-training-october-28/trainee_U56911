package com.example.demo.service;

import com.example.demo.dto.KafkaTaskMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

/**
 * Service for producing Kafka messages using Spring Cloud Stream
 */
@Service
public class KafkaMessageProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducerService.class);
    
    private final StreamBridge streamBridge;
    
    public KafkaMessageProducerService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }
    
    /**
     * Send a task creation event to Kafka
     */
    public void sendTaskCreatedEvent(Long taskId, String taskTitle, Long userId, String userName) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TASK_CREATED", 
            taskId, 
            taskTitle, 
            "PENDING", 
            userId, 
            userName
        );
        sendMessage(message);
    }
    
    /**
     * Send a task update event to Kafka
     */
    public void sendTaskUpdatedEvent(Long taskId, String taskTitle, String taskStatus, 
                                     Long userId, String userName) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TASK_UPDATED", 
            taskId, 
            taskTitle, 
            taskStatus, 
            userId, 
            userName
        );
        sendMessage(message);
    }
    
    /**
     * Send a task completed event to Kafka
     */
    public void sendTaskCompletedEvent(Long taskId, String taskTitle, Long userId, String userName) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TASK_COMPLETED", 
            taskId, 
            taskTitle, 
            "COMPLETED", 
            userId, 
            userName
        );
        sendMessage(message);
    }
    
    /**
     * Send a task deleted event to Kafka
     */
    public void sendTaskDeletedEvent(Long taskId, String taskTitle, Long userId, String userName) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            "TASK_DELETED", 
            taskId, 
            taskTitle, 
            "DELETED", 
            userId, 
            userName
        );
        sendMessage(message);
    }
    
    /**
     * Send a custom event to Kafka
     */
    public void sendCustomEvent(String eventType, Long taskId, String taskTitle, 
                                String taskStatus, Long userId, String userName, 
                                String additionalInfo) {
        KafkaTaskMessageDTO message = new KafkaTaskMessageDTO(
            eventType, 
            taskId, 
            taskTitle, 
            taskStatus, 
            userId, 
            userName
        );
        message.setAdditionalInfo(additionalInfo);
        sendMessage(message);
    }
    
    /**
     * Generic method to send any Kafka message
     */
    public void sendMessage(KafkaTaskMessageDTO message) {
        try {
            boolean sent = streamBridge.send("taskOutput", message);
            if (sent) {
                logger.info("Successfully sent Kafka message: {}", message);
            } else {
                logger.error("Failed to send Kafka message: {}", message);
            }
        } catch (Exception e) {
            logger.error("Error sending Kafka message: {}", message, e);
        }
    }
    
    /**
     * Send a test message to Kafka
     */
    public void sendTestMessage() {
        KafkaTaskMessageDTO testMessage = new KafkaTaskMessageDTO(
            "TEST_EVENT",
            999L,
            "Test Task",
            "TEST",
            1L,
            "Test User"
        );
        testMessage.setAdditionalInfo("This is a test message from Spring Cloud Stream Kafka demo");
        sendMessage(testMessage);
    }
}
