package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.StructuredConcurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller demonstrating Java 21 structured concurrency features
 * Exposes endpoints that use modern Java structured concurrency patterns
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private StructuredConcurrencyService structuredConcurrencyService;
    
    /**
     * Get project dashboard data using structured concurrency
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<StructuredConcurrencyService.ProjectDashboardData>> getProjectDashboard(@PathVariable Long projectId) {
        var dashboardData = structuredConcurrencyService.getProjectDashboardData(projectId);
        
        return ResponseEntity.ok(ApiResponse.success(
            dashboardData,
            "Project dashboard data retrieved using structured concurrency"
        ));
    }
    
    /**
     * Calculate project statistics
     */
    @GetMapping("/project/{projectId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectStats(@PathVariable Long projectId) {
        var dashboardData = structuredConcurrencyService.getProjectDashboardData(projectId);
        var stats = dashboardData.stats();
        
        Map<String, Object> statsMap = Map.of(
            "totalTasks", stats.totalTasks(),
            "completedTasks", stats.completedTasks(),
            "inProgressTasks", stats.inProgressTasks(),
            "overdueTasks", stats.overdueTasks(),
            "completionRate", stats.totalTasks() > 0 ? 
                (double) stats.completedTasks() / stats.totalTasks() * 100 : 0
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            statsMap,
            "Project statistics calculated"
        ));
    }
    
    /**
     * Get project tasks overview
     */
    @GetMapping("/project/{projectId}/tasks")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProjectTasks(@PathVariable Long projectId) {
        var dashboardData = structuredConcurrencyService.getProjectDashboardData(projectId);
        
        Map<String, Object> tasksOverview = Map.of(
            "project", dashboardData.project(),
            "tasks", dashboardData.tasks(),
            "totalTasks", dashboardData.tasks().size(),
            "teamMembers", dashboardData.teamMembers()
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            tasksOverview,
            "Project tasks overview retrieved"
        ));
    }
}
