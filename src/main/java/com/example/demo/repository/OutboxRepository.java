package com.example.demo.repository;

import com.example.demo.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Outbox Pattern messages
 * 
 * Provides methods to:
 * - Find unpublished messages
 * - Find messages that failed and should be retried
 * - Mark messages as published
 */
@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    /**
     * Find all unpublished messages
     */
    @Query("SELECT o FROM OutboxMessage o WHERE o.published = false ORDER BY o.createdAt ASC")
    List<OutboxMessage> findUnpublishedMessages();

    /**
     * Find unpublished messages with limit for batch processing
     */
    @Query("SELECT o FROM OutboxMessage o WHERE o.published = false AND o.retryCount < :maxRetries ORDER BY o.createdAt ASC LIMIT :limit")
    List<OutboxMessage> findUnpublishedMessagesWithLimit(@Param("limit") int limit, @Param("maxRetries") int maxRetries);

    /**
     * Find messages that need retry (failed previously but within retry limit)
     */
    @Query("SELECT o FROM OutboxMessage o WHERE o.published = false AND o.retryCount < :maxRetries AND (o.lastError IS NOT NULL) ORDER BY o.createdAt ASC")
    List<OutboxMessage> findMessagesToRetry(@Param("maxRetries") int maxRetries);

    /**
     * Mark a message as published
     */
    @Modifying
    @Query("UPDATE OutboxMessage o SET o.published = true, o.publishedAt = :publishedAt WHERE o.id = :id")
    void markAsPublished(@Param("id") Long id, @Param("publishedAt") LocalDateTime publishedAt);

    /**
     * Increment retry count and save error message
     */
    @Modifying
    @Query("UPDATE OutboxMessage o SET o.retryCount = o.retryCount + 1, o.lastError = :error WHERE o.id = :id")
    void incrementRetryCount(@Param("id") Long id, @Param("error") String error);

    /**
     * Count unpublished messages
     */
    @Query("SELECT COUNT(o) FROM OutboxMessage o WHERE o.published = false")
    long countUnpublished();

    /**
     * Delete old published messages (cleanup)
     */
    @Modifying
    @Query("DELETE FROM OutboxMessage o WHERE o.published = true AND o.publishedAt < :before")
    int deleteOldPublishedMessages(@Param("before") LocalDateTime before);
}
