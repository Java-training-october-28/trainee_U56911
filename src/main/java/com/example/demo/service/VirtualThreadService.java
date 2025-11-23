package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating Java 21 virtual threads for I/O operations
 */
@Service
public class VirtualThreadService {
    
    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadService.class);
    private final TaskRepository taskRepository;
    
    // Virtual thread executor for I/O operations
    private final ExecutorService virtualThreadExecutor;
    
    public VirtualThreadService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        // Create virtual thread executor
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }
    
    /**
     * Demonstrates virtual threads for async database operations
     */
    public CompletableFuture<List<Task>> getTasksByProjectAsync(Long projectId) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Virtual thread executing database query for project: {}", projectId);
            try {
                // Simulate I/O operation
                Thread.sleep(100);
                return taskRepository.findByProjectId(projectId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Virtual thread interrupted", e);
            }
        }, virtualThreadExecutor);
    }
    
    /**
     * Demonstrates multiple concurrent virtual threads
     */
    public CompletableFuture<Void> processMultipleTasksAsync(List<Long> taskIds) {
        List<CompletableFuture<Void>> futures = taskIds.stream()
            .map(taskId -> CompletableFuture.runAsync(() -> {
                logger.info("Virtual thread processing task: {}", taskId);
                try {
                    // Simulate I/O operation
                    Thread.sleep(50);
                    // Update task status
                    taskRepository.findById(taskId).ifPresent(task -> {
                        task.setStatus(TaskStatus.IN_PROGRESS);
                        task.setUpdatedAt(LocalDateTime.now());
                        taskRepository.save(task);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Virtual thread interrupted", e);
                }
            }, virtualThreadExecutor))
            .toList();
            
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    /**
     * Demonstrates virtual threads for batch processing
     */
    public CompletableFuture<Integer> updateOverdueTasksAsync() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Virtual thread processing overdue tasks");
            try {
                List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
                
                // Process each task in parallel using virtual threads
                List<CompletableFuture<Void>> updateFutures = overdueTasks.stream()
                    .map(task -> CompletableFuture.runAsync(() -> {
                        task.setStatus(TaskStatus.PENDING);
                        task.setUpdatedAt(LocalDateTime.now());
                        taskRepository.save(task);
                        logger.info("Updated overdue task: {}", task.getId());
                    }, virtualThreadExecutor))
                    .toList();
                
                // Wait for all updates to complete
                CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0])).join();
                return overdueTasks.size();
                
            } catch (Exception e) {
                logger.error("Error processing overdue tasks", e);
                throw new RuntimeException("Failed to process overdue tasks", e);
            }
        }, virtualThreadExecutor);
    }
    
    /**
     * Demonstrates virtual threads with timeout
     */
    public CompletableFuture<String> processTaskWithTimeout(Long taskId, Duration timeout) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Virtual thread processing task with timeout: {}", taskId);
            try {
                // Simulate long-running operation
                Thread.sleep(timeout.toMillis() + 100); // Exceed timeout
                return "Task " + taskId + " completed";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Virtual thread interrupted", e);
            }
        }, virtualThreadExecutor)
        .orTimeout(timeout.toSeconds(), TimeUnit.SECONDS)
        .exceptionally(throwable -> {
            logger.warn("Task {} timed out: {}", taskId, throwable.getMessage());
            return "Task " + taskId + " timed out";
        });
    }
    
    /**
     * Demonstrates virtual thread performance comparison
     */
    public void demonstrateVirtualThreadPerformance() {
        int numberOfTasks = 1000;
        logger.info("Starting virtual thread performance test with {} tasks", numberOfTasks);
        
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Void>> futures = java.util.stream.IntStream.range(0, numberOfTasks)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                try {
                    // Simulate I/O operation
                    Thread.sleep(10);
                    logger.debug("Virtual thread completed task {}", i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, virtualThreadExecutor))
            .toList();
        
        // Wait for all tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("Virtual threads completed {} tasks in {} ms", numberOfTasks, duration);
        logger.info("Average time per task: {} ms", (double) duration / numberOfTasks);
    }
    
    /**
     * Clean up executor service
     */
    public void shutdown() {
        if (virtualThreadExecutor != null && !virtualThreadExecutor.isShutdown()) {
            virtualThreadExecutor.shutdown();
            try {
                if (!virtualThreadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    virtualThreadExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                virtualThreadExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
