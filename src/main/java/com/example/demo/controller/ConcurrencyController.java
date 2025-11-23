package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.VirtualThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    /**
     * Get tasks by project using virtual threads for async database operations
     */
    @GetMapping("/tasks/project/{projectId}")
    public ResponseEntity<ApiResponse<String>> getTasksByProjectAsync(@PathVariable Long projectId) {
        var future = virtualThreadService.getTasksByProjectAsync(projectId);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Task fetch initiated with virtual threads for project: " + projectId,
            "Check logs for completion status"
        ));
    }
    
    /**
     * Process multiple tasks concurrently with virtual threads
     */
    @PostMapping("/tasks/batch-process")
    public ResponseEntity<ApiResponse<String>> batchProcessTasks(@RequestBody List<Long> taskIds) {
        var future = virtualThreadService.processMultipleTasksAsync(taskIds);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Batch task processing initiated with virtual threads",
            "Processing " + taskIds.size() + " tasks concurrently"
        ));
    }
    
    /**
     * Update overdue tasks using virtual threads
     */
    @PutMapping("/tasks/overdue/async")
    public ResponseEntity<ApiResponse<String>> updateOverdueTasksAsync() {
        var future = virtualThreadService.updateOverdueTasksAsync();
        
        return ResponseEntity.ok(ApiResponse.success(
            "Overdue task update initiated with virtual threads",
            "Check logs for completion status"
        ));
    }
    
    /**
     * Process task with timeout using virtual threads
     */
    @PostMapping("/tasks/{taskId}/timeout")
    public ResponseEntity<ApiResponse<String>> processTaskWithTimeout(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "5") long timeoutSeconds) {
        
        var future = virtualThreadService.processTaskWithTimeout(taskId, java.time.Duration.ofSeconds(timeoutSeconds));
        
        return ResponseEntity.ok(ApiResponse.success(
            "Task processing with timeout initiated",
            "Task " + taskId + " will timeout after " + timeoutSeconds + " seconds"
        ));
    }
    
    /**
     * Demonstrate virtual thread performance
     */
    @GetMapping("/performance/demo")
    public ResponseEntity<ApiResponse<String>> demonstrateVirtualThreadPerformance() {
        virtualThreadService.demonstrateVirtualThreadPerformance();
        
        return ResponseEntity.ok(ApiResponse.success(
            "Virtual thread performance demonstration completed",
            "Check logs for performance metrics"
        ));
    }
}
