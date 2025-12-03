package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Task;
import com.example.demo.service.VirtualThreadService;
import com.example.demo.service.StructuredConcurrencyService;
import com.example.demo.service.VirtualThreadExecutorDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller demonstrating Java 21 virtual threads and concurrency features
 * Exposes endpoints that use modern Java concurrency patterns
 */
@RestController
@RequestMapping("/api/concurrency")
public class ConcurrencyController {
    
    @Autowired
    private VirtualThreadService virtualThreadService;
    
    @Autowired
    private StructuredConcurrencyService structuredConcurrencyService;
    
    @Autowired
    private VirtualThreadExecutorDemoService virtualThreadExecutorDemoService;
    
    /**
     * Get tasks by project using virtual threads for async database operations
     * Returns actual task data from CompletableFuture
     */
    @GetMapping("/tasks/project/{projectId}")
    public CompletableFuture<ResponseEntity<ApiResponse<List<Task>>>> getTasksByProjectAsync(@PathVariable Long projectId) {
        return virtualThreadService.getTasksByProjectAsync(projectId)
            .thenApply(tasks -> ResponseEntity.ok(ApiResponse.success(
                tasks,
                "Tasks retrieved successfully using virtual threads for project: " + projectId
            )))
            .exceptionally(throwable -> ResponseEntity.ok(ApiResponse.error(
                "Failed to retrieve tasks for project: " + projectId,
                throwable.getMessage()
            )));
    }
    
    /**
     * Process multiple tasks concurrently with virtual threads
     * Returns completion status from CompletableFuture
     */
    @PostMapping("/tasks/batch-process")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> batchProcessTasks(@RequestBody List<Long> taskIds) {
        return virtualThreadService.processMultipleTasksAsync(taskIds)
            .thenApply(voidResult -> ResponseEntity.ok(ApiResponse.success(
                "Batch processing completed successfully",
                "Processed " + taskIds.size() + " tasks concurrently using virtual threads"
            )))
            .exceptionally(throwable -> ResponseEntity.ok(ApiResponse.error(
                "Batch processing failed",
                throwable.getMessage()
            )));
    }
    
    /**
     * Update overdue tasks using virtual threads
     * Returns count of updated tasks from CompletableFuture
     */
    @PutMapping("/tasks/overdue/async")
    public CompletableFuture<ResponseEntity<ApiResponse<Integer>>> updateOverdueTasksAsync() {
        return virtualThreadService.updateOverdueTasksAsync()
            .thenApply(updatedCount -> ResponseEntity.ok(ApiResponse.success(
                updatedCount,
                "Updated " + updatedCount + " overdue tasks using virtual threads"
            )))
            .exceptionally(throwable -> ResponseEntity.ok(ApiResponse.error(
                "Failed to update overdue tasks",
                throwable.getMessage()
            )));
    }
    
    /**
     * Process task with timeout using virtual threads
     * Returns timeout result from CompletableFuture
     */
    @PostMapping("/tasks/{taskId}/timeout")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> processTaskWithTimeout(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "5") long timeoutSeconds) {
        
        return virtualThreadService.processTaskWithTimeout(taskId, Duration.ofSeconds(timeoutSeconds))
            .thenApply(result -> ResponseEntity.ok(ApiResponse.success(
                result,
                "Task processing completed with timeout handling"
            )))
            .exceptionally(throwable -> ResponseEntity.ok(ApiResponse.error(
                "Task processing failed",
                throwable.getMessage()
            )));
    }
    
    /**
     * Demonstrate virtual thread performance
     * Returns performance metrics
     */
    @GetMapping("/performance/demo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demonstrateVirtualThreadPerformance() {
        long startTime = System.currentTimeMillis();
        virtualThreadService.demonstrateVirtualThreadPerformance();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        Map<String, Object> performanceMetrics = Map.of(
            "totalTasks", 1000,
            "totalTimeMs", duration,
            "averageTimePerTaskMs", (double) duration / 1000,
            "threadType", "Virtual Threads"
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            performanceMetrics,
            "Virtual thread performance demonstration completed"
        ));
    }
    
    /**
     * Get project dashboard data using structured concurrency
     * Demonstrates concurrent data fetching with automatic scope management
     */
    @GetMapping("/structured/project/{projectId}/dashboard")
    public ResponseEntity<ApiResponse<StructuredConcurrencyService.ProjectDashboardData>> getProjectDashboardStructured(@PathVariable Long projectId) {
        try {
            var dashboardData = structuredConcurrencyService.getProjectDashboardData(projectId);
            return ResponseEntity.ok(ApiResponse.success(
                dashboardData,
                "Project dashboard data retrieved using structured concurrency"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to fetch project dashboard data",
                e.getMessage()
            ));
        }
    }
    
    /**
     * Process tasks with timeout using structured concurrency
     * Demonstrates ShutdownOnSuccess pattern
     */
    @PostMapping("/structured/tasks/timeout")
    public ResponseEntity<ApiResponse<StructuredConcurrencyService.TaskProcessingResult>> processTasksWithTimeout(
            @RequestBody List<Long> taskIds,
            @RequestParam(defaultValue = "10") long timeoutSeconds) {
        
        try {
            var result = structuredConcurrencyService.processTasksWithTimeout(taskIds, timeoutSeconds);
            return ResponseEntity.ok(ApiResponse.success(
                result,
                "Task processing completed with structured concurrency timeout"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to process tasks with timeout",
                e.getMessage()
            ));
        }
    }
    
    /**
     * Process batch of tasks using structured concurrency
     * Demonstrates ShutdownOnFailure pattern and result collection
     */
    @PostMapping("/structured/tasks/batch")
    public ResponseEntity<ApiResponse<StructuredConcurrencyService.BatchTaskResult>> processBatchTasksStructured(@RequestBody List<Long> taskIds) {
        try {
            var result = structuredConcurrencyService.processBatchTasks(taskIds);
            return ResponseEntity.ok(ApiResponse.success(
                result,
                "Batch task processing completed using structured concurrency"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to process batch tasks",
                e.getMessage()
            ));
        }
    }
    
    /**
     * Demonstrate structured concurrency performance
     * Returns performance metrics comparing different patterns
     */
    @GetMapping("/structured/performance/demo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demonstrateStructuredConcurrencyPerformance() {
        long startTime = System.currentTimeMillis();
        
        // Simulate structured concurrency operations
        try {
            // Test batch processing with 100 tasks
            var taskIds = java.util.stream.LongStream.range(1, 101)
                .boxed()
                .toList();
            
            var batchResult = structuredConcurrencyService.processBatchTasks(taskIds);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            Map<String, Object> performanceMetrics = Map.of(
                "totalTasks", taskIds.size(),
                "successfulTasks", batchResult.successfulResults().size(),
                "failedTasks", batchResult.failedCount(),
                "totalTimeMs", duration,
                "averageTimePerTaskMs", (double) duration / taskIds.size(),
                "concurrencyPattern", "StructuredTaskScope",
                "scopeType", "ShutdownOnFailure"
            );
            
            return ResponseEntity.ok(ApiResponse.success(
                performanceMetrics,
                "Structured concurrency performance demonstration completed"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to demonstrate structured concurrency performance",
                e.getMessage()
            ));
        }
    }
    
    /**
     * Demonstrate why Executors.newVirtualThreadPerTaskExecutor() returns ExecutorService
     * Returns comprehensive explanation with live execution results
     */
    @GetMapping("/executor/demo")
    public ResponseEntity<ApiResponse<VirtualThreadExecutorDemoService.ExecutorDemoResult>> demonstrateExecutorService() {
        try {
            var demoResult = virtualThreadExecutorDemoService.runDemo();
            return ResponseEntity.ok(ApiResponse.success(
                demoResult,
                "ExecutorService demonstration completed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(
                "Failed to demonstrate ExecutorService capabilities",
                e.getMessage()
            ));
        }
    }
}
