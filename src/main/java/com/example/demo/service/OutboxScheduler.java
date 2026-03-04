package com.example.demo.service;

import com.example.demo.entity.OutboxMessage;
import com.example.demo.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbox Scheduler - Publishes messages from outbox table to Kafka
 * 
 * This is the KEY component of the Outbox Pattern:
 * 1. Runs on a schedule (e.g., every 10 seconds)
 * 2. Reads unpublished messages from outbox table
 * 3. Publishes each message to Kafka
 * 4. Marks message as published after successful send
 * 
 * If Kafka fails:
 * - Message stays in outbox for retry
 * - Retry count is incremented
 * - DB transaction is NOT affected
 */
@Component
public class OutboxScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutboxScheduler.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.batch-size:100}")
    private int batchSize;

    @Value("${app.outbox.max-retries:5}")
    private int maxRetries;

    @Value("${app.outbox.topics.task:task-events}")
    private String taskEventsTopic;

    @Value("${app.outbox.topics.order:order-events}")
    private String orderEventsTopic;

    public OutboxScheduler(OutboxRepository outboxRepository,
                          KafkaTemplate<String, String> kafkaTemplate,
                          ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Scheduled task to process outbox messages
     * Runs every 10 seconds (configurable)
     */
    @Scheduled(fixedDelayString = "${app.outbox.poll-interval:10000}")
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findUnpublishedMessagesWithLimit(batchSize, maxRetries);
        
        if (messages.isEmpty()) {
            logger.debug("No outbox messages to process");
            return;
        }

        logger.info("Processing {} outbox messages", messages.size());

        for (OutboxMessage message : messages) {
            try {
                publishToKafka(message);
                outboxRepository.markAsPublished(message.getId(), LocalDateTime.now());
                logger.info("✅ Published outbox message: {} - {} - {}", 
                    message.getAggregateType(), message.getAggregateId(), message.getEventType());
            } catch (Exception e) {
                logger.error("❌ Failed to publish outbox message: {}", message.getId(), e);
                handleFailure(message, e.getMessage());
            }
        }
    }

    /**
     * Publish a single outbox message to Kafka
     */
    private void publishToKafka(OutboxMessage message) throws Exception {
        String topic = getTopicForAggregateType(message.getAggregateType());
        String key = message.getAggregateId();
        String payload = message.getPayload();

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
        
        // Add event type header
        record.headers().add("event-type", message.getEventType().getBytes());
        record.headers().add("aggregate-type", message.getAggregateType().getBytes());

        kafkaTemplate.send(record).get(); // Synchronous send for reliability
    }

    /**
     * Get Kafka topic based on aggregate type
     */
    private String getTopicForAggregateType(String aggregateType) {
        return switch (aggregateType.toLowerCase()) {
            case "task" -> taskEventsTopic;
            case "order" -> orderEventsTopic;
            default -> "default-events";
        };
    }

    /**
     * Handle failure - increment retry count
     */
    private void handleFailure(OutboxMessage message, String error) {
        if (message.getRetryCount() >= maxRetries) {
            logger.error("❌ Message {} exceeded max retries, moving to dead letter", message.getId());
            // Could implement dead letter queue here
        } else {
            outboxRepository.incrementRetryCount(message.getId(), error);
        }
    }

    /**
     * Cleanup old published messages
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "${app.outbox.cleanup-cron:0 0 2 * * *}")
    @Transactional
    public void cleanupOldMessages() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7); // Keep 7 days
        int deleted = outboxRepository.deleteOldPublishedMessages(cutoff);
        logger.info("Cleaned up {} old outbox messages", deleted);
    }

    /**
     * Health check - get count of unpublished messages
     */
    public long getUnpublishedCount() {
        return outboxRepository.countUnpublished();
    }
}
