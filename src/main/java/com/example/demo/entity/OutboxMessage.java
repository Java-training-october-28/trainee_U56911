package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Outbox Message Entity - Used for the Outbox Pattern
 * 
 * The Outbox Pattern solves the dual-write problem:
 * 1. Write to DB + Write to outbox table in SAME transaction
 * 2. A scheduler reads from outbox and publishes to Kafka
 * 3. Mark message as published after successful send
 * 
 * This ensures:
 * ✅ DB and Kafka are ALWAYS consistent
 * ✅ If Kafka fails, message stays in outbox for retry
 * ✅ No dual-write problem
 * ✅ Most reliable approach for critical systems
 */
@Entity
@Table(name = "outbox_messages")
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;  // e.g., "Task", "Order", "User"

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;     // ID of the entity

    @Column(name = "event_type", nullable = false)
    private String eventType;       // e.g., "TASK_CREATED", "ORDER_PLACED"

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;          // JSON payload for Kafka

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "published", nullable = false)
    private boolean published = false;

    // Constructors
    public OutboxMessage() {
        this.createdAt = LocalDateTime.now();
    }

    public OutboxMessage(String aggregateType, String aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }

    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    @Override
    public String toString() {
        return "OutboxMessage{" +
                "id=" + id +
                ", aggregateType='" + aggregateType + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", published=" + published +
                ", retryCount=" + retryCount +
                '}';
    }
}
