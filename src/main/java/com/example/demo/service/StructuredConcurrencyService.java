package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Service demonstrating Java 21 structured concurrency for complex operations
 * NOTE: StructuredTaskScope is a preview API in Java 21 and requires --enable-preview flag
 */
@Service
public class StructuredConcurrencyService {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuredConcurrencyService.class);
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    public StructuredConcurrencyService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Demonstrates structured concurrency for fetching project dashboard data
     * All subtasks are automatically managed within a scope
     * NOTE: This requires --enable-preview flag to compile
     */
    public ProjectDashboardData getProjectDashboardData(Long projectId) {
        logger.info("Fetching project dashboard data using structured concurrency for project: {}", projectId);
        
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Launch concurrent subtasks
            var projectSubtask = scope.fork(() -> 
                projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found: " + projectId))
            );
            
            var tasksSubtask = scope.fork(() -> 
                taskRepository.findByProjectId(projectId)
            );
            
            var statsSubtask = scope.fork(() -> 
                calculateProjectStats(projectId)
            );
            
            // For now, we'll skip team members as the repository method doesn't exist
            // var teamMembersSubtask = scope.fork(() -> 
            //     userRepository.findByProjects_Id(projectId)
            // );
            
            // Wait for all subtasks to complete or fail
            try {
                scope.join();           // Wait for all subtasks
                scope.throwIfFailed();  // Propagate exception if any subtask failed
                
                // All subtasks completed successfully
                Project project = projectSubtask.get();
                List<Task> tasks = tasksSubtask.get();
                ProjectStats stats = statsSubtask.get();
                // List<User> teamMembers = teamMembersSubtask.get();
                List<User> teamMembers = List.of(); // Empty list for now
                
                logger.info("Successfully fetched dashboard data for project {}: {} tasks, {} team members", 
                    projectId, tasks.size(), teamMembers.size());
                
                return new ProjectDashboardData(project, tasks, teamMembers, stats);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Dashboard data fetch interrupted", e);
            }
        } catch (Exception e) {
            logger.error("Error fetching project dashboard data for project {}: {}", projectId, e.getMessage());
            throw new RuntimeException("Failed to fetch project dashboard data", e);
        }
    }
    
    /**
     * Process multiple tasks concurrently using structured concurrency
     * Demonstrates ShutdownOnSuccess pattern for first successful result
     */
    public TaskProcessingResult processTasksWithTimeout(List<Long> taskIds, long timeoutSeconds) {
        logger.info("Processing {} tasks with timeout of {} seconds", taskIds.size(), timeoutSeconds);
        
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<TaskProcessingResult>()) {
            // Fork subtasks for each task
            for (Long taskId : taskIds) {
                scope.fork(() -> processSingleTask(taskId));
            }
            
            // Wait for first successful result or timeout
            try {
                TaskProcessingResult result = scope.joinUntil(
                    java.time.Instant.now().plusSeconds(timeoutSeconds)
                ).result();
                
                logger.info("Successfully processed task {} within timeout", result.taskId());
                return result;
                
            } catch (TimeoutException e) {
                logger.warn("Timeout occurred while processing tasks");
                return new TaskProcessingResult(null, "TIMEOUT", "Processing timeout exceeded");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task processing interrupted", e);
            }
        } catch (Exception e) {
            logger.error("Error processing tasks: {}", e.getMessage());
            throw new RuntimeException("Failed to process tasks", e);
        }
    }
    
    /**
     * Process batch of tasks with structured concurrency and collect all results
     */
    public BatchTaskResult processBatchTasks(List<Long> taskIds) {
        logger.info("Processing batch of {} tasks using structured concurrency", taskIds.size());
        
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork subtasks for each task
            var subtasks = taskIds.stream()
                .map(taskId -> scope.fork(() -> processSingleTask(taskId)))
                .toList();
            
            // Wait for all subtasks to complete
            try {
                scope.join();
                scope.throwIfFailed();
                
                // Collect results
                List<TaskProcessingResult> successfulResults = subtasks.stream()
                    .map(subtask -> subtask.get())
                    .filter(result -> "SUCCESS".equals(result.status()))
                    .toList();
                
                int failedCount = taskIds.size() - successfulResults.size();
                
                logger.info("Batch processing completed: {} successful, {} failed", 
                    successfulResults.size(), failedCount);
                
                return new BatchTaskResult(successfulResults, failedCount, taskIds.size());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Batch processing interrupted", e);
            }
        } catch (Exception e) {
            logger.error("Error in batch processing: {}", e.getMessage());
            throw new RuntimeException("Failed to process batch tasks", e);
        }
    }
    
    /**
     * Process a single task (simulated processing)
     */
    private TaskProcessingResult processSingleTask(Long taskId) {
        try {
            // Simulate some processing time
            Thread.sleep(100 + (long)(Math.random() * 400));
            
            // Simulate occasional failures
            if (Math.random() < 0.1) { // 10% failure rate
                return new TaskProcessingResult(taskId, "FAILED", "Simulated processing failure");
            }
            
            return new TaskProcessingResult(taskId, "SUCCESS", "Task processed successfully");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new TaskProcessingResult(taskId, "INTERRUPTED", "Processing interrupted");
        }
    }
    
    /**
     * Helper method to calculate project statistics
     */
    private ProjectStats calculateProjectStats(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
        long inProgressTasks = tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
        long overdueTasks = tasks.stream()
            .filter(task -> task.getDueDate() != null && 
                           task.getDueDate().isBefore(LocalDateTime.now()) &&
                           task.getStatus() != TaskStatus.COMPLETED)
            .count();
        
        return new ProjectStats(totalTasks, completedTasks, inProgressTasks, overdueTasks);
    }
    
    // Record classes for structured concurrency results
    
    public record ProjectDashboardData(
        Project project,
        List<Task> tasks,
        List<User> teamMembers,
        ProjectStats stats
    ) {}
    
    public record ProjectStats(
        long totalTasks,
        long completedTasks,
        long inProgressTasks,
        long overdueTasks
    ) {}
    
    public record TaskProcessingResult(
        Long taskId,
        String status,
        String message
    ) {}
    
    public record BatchTaskResult(
        List<TaskProcessingResult> successfulResults,
        int failedCount,
        int totalProcessed
    ) {}
}
