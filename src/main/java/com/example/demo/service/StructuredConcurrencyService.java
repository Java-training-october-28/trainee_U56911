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
    /*
    public ProjectDashboardData getProjectDashboardData(Long projectId) {
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
            
            // Wait for all subtasks to complete or fail
            try {
                scope.join();           // Wait for all subtasks
                scope.throwIfFailed();  // Propagate exception if any subtask failed
                
                // All subtasks completed successfully
                Project project = projectSubtask.get();
                List<Task> tasks = tasksSubtask.get();
                ProjectStats stats = statsSubtask.get();
                
                return new ProjectDashboardData(project, tasks, List.of(), stats);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Dashboard data fetch interrupted", e);
            }
        }
    }
    */
    
    /**
     * Alternative implementation using CompletableFuture for structured concurrency
     * This works without preview features
     */
    public ProjectDashboardData getProjectDashboardData(Long projectId) {
        logger.info("Fetching project dashboard data for project: {}", projectId);
        
        // Fetch data sequentially for now (can be enhanced with CompletableFuture)
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
        
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        ProjectStats stats = calculateProjectStats(projectId);
        
        return new ProjectDashboardData(project, tasks, List.of(), stats);
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
