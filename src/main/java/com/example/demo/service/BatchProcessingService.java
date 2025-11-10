package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling batch processing operations on large datasets
 * Demonstrates efficient processing of large numbers of records
 */
@Service
@Transactional
public class BatchProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(BatchProcessingService.class);

    @Autowired
    private TaskRepository taskRepository;

    private static final int BATCH_SIZE = 100; // Process 100 records at a time

    /**
     * Batch update task status for all tasks in a project
     * Uses pagination to process large datasets efficiently
     */
    public BatchProcessingResult updateTaskStatusInBatch(Long projectId, String newStatus) {
        logger.info("Starting batch status update for project {} to status {}", projectId, newStatus);
        
        int totalProcessed = 0;
        int totalUpdated = 0;
        int pageNumber = 0;
        List<Long> processedTaskIds = new ArrayList<>();
        
        Page<Task> taskPage;
        do {
            Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
            taskPage = taskRepository.findTasksByProjectIdForBatchProcessing(projectId, pageable);
            
            List<Long> taskIds = taskPage.getContent().stream()
                    .map(Task::getId)
                    .toList();
            
            if (!taskIds.isEmpty()) {
                int updatedCount = taskRepository.updateTaskStatusInBulk(taskIds, newStatus);
                totalUpdated += updatedCount;
                processedTaskIds.addAll(taskIds);
                logger.debug("Processed batch {}: {} tasks, {} updated", pageNumber, taskIds.size(), updatedCount);
            }
            
            totalProcessed += taskPage.getNumberOfElements();
            pageNumber++;
            
        } while (taskPage.hasNext());
        
        BatchProcessingResult result = new BatchProcessingResult(totalProcessed, totalUpdated, processedTaskIds);
        logger.info("Batch status update completed: {}", result);
        
        return result;
    }

    /**
     * Batch reassign tasks to a new assignee
     */
    public BatchProcessingResult reassignTasksInBatch(List<Long> taskIds, Long newAssigneeId) {
        logger.info("Starting batch reassignment of {} tasks to assignee {}", taskIds.size(), newAssigneeId);
        
        // Process in batches to avoid memory issues
        int totalUpdated = 0;
        List<Long> processedTaskIds = new ArrayList<>();
        
        for (int i = 0; i < taskIds.size(); i += BATCH_SIZE) {
            List<Long> batch = taskIds.subList(i, Math.min(i + BATCH_SIZE, taskIds.size()));
            
            int updatedCount = taskRepository.reassignTasksInBulk(batch, newAssigneeId);
            totalUpdated += updatedCount;
            processedTaskIds.addAll(batch);
            
            logger.debug("Processed reassignment batch: {} tasks, {} updated", batch.size(), updatedCount);
        }
        
        BatchProcessingResult result = new BatchProcessingResult(taskIds.size(), totalUpdated, processedTaskIds);
        logger.info("Batch reassignment completed: {}", result);
        
        return result;
    }

    /**
     * Batch update timestamps for tasks that haven't been updated recently
     */
    public BatchProcessingResult refreshTaskTimestampsInBatch(LocalDateTime cutoffDate) {
        logger.info("Starting batch timestamp refresh for tasks older than {}", cutoffDate);
        
        int totalProcessed = 0;
        int totalUpdated = 0;
        int pageNumber = 0;
        List<Long> processedTaskIds = new ArrayList<>();
        
        Page<Task> taskPage;
        do {
            Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
            taskPage = taskRepository.findAll(pageable);
            
            List<Long> taskIds = taskPage.getContent().stream()
                    .filter(task -> task.getUpdatedAt().isBefore(cutoffDate))
                    .map(Task::getId)
                    .toList();
            
            if (!taskIds.isEmpty()) {
                int updatedCount = taskRepository.updateTimestampsForTasks(taskIds);
                totalUpdated += updatedCount;
                processedTaskIds.addAll(taskIds);
                logger.debug("Processed timestamp batch {}: {} tasks, {} updated", pageNumber, taskIds.size(), updatedCount);
            }
            
            totalProcessed += taskPage.getNumberOfElements();
            pageNumber++;
            
        } while (taskPage.hasNext());
        
        BatchProcessingResult result = new BatchProcessingResult(totalProcessed, totalUpdated, processedTaskIds);
        logger.info("Batch timestamp refresh completed: {}", result);
        
        return result;
    }

    /**
     * Process overdue tasks in batch (mark as overdue, send notifications, etc.)
     */
    public BatchProcessingResult processOverdueTasksInBatch() {
        LocalDateTime currentDate = LocalDateTime.now();
        logger.info("Starting batch processing of overdue tasks as of {}", currentDate);
        
        List<Task> overdueTasks = taskRepository.findOverdueTasks(currentDate);
        List<Long> taskIds = overdueTasks.stream()
                .map(Task::getId)
                .toList();
        
        if (!taskIds.isEmpty()) {
            // Mark tasks as overdue status
            int updatedCount = taskRepository.updateTaskStatusInBulk(taskIds, "OVERDUE");
            
            BatchProcessingResult result = new BatchProcessingResult(overdueTasks.size(), updatedCount, taskIds);
            logger.info("Batch overdue task processing completed: {}", result);
            
            return result;
        }
        
        logger.info("No overdue tasks found for processing");
        return new BatchProcessingResult(0, 0, List.of());
    }

    /**
     * Result class for batch processing operations
     */
    public static class BatchProcessingResult {
        private final int totalProcessed;
        private final int totalUpdated;
        private final List<Long> processedTaskIds;

        public BatchProcessingResult(int totalProcessed, int totalUpdated, List<Long> processedTaskIds) {
            this.totalProcessed = totalProcessed;
            this.totalUpdated = totalUpdated;
            this.processedTaskIds = processedTaskIds;
        }

        public int getTotalProcessed() {
            return totalProcessed;
        }

        public int getTotalUpdated() {
            return totalUpdated;
        }

        public List<Long> getProcessedTaskIds() {
            return processedTaskIds;
        }

        @Override
        public String toString() {
            return String.format("BatchProcessingResult{processed=%d, updated=%d, taskIds=%d}",
                    totalProcessed, totalUpdated, processedTaskIds.size());
        }
    }
}
