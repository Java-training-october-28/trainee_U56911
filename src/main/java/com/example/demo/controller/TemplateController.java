package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.*;
import com.example.demo.service.StringTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating Java 21 string templates and enhanced APIs
 * Exposes endpoints that use modern Java string and collection features
 */
@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    
    @Autowired
    private StringTemplateService stringTemplateService;
    
    /**
     * Generate task search query using text blocks
     */
    @GetMapping("/task-search-query")
    public ResponseEntity<ApiResponse<String>> generateTaskSearchQuery(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority) {
        
        String query = stringTemplateService.generateTaskSearchQuery(searchTerm, status, priority);
        
        return ResponseEntity.ok(ApiResponse.success(
            query,
            "Task search query generated using text blocks"
        ));
    }
    
    
    /**
     * Generate validation error message using text blocks
     */
    @GetMapping("/validation-error")
    public ResponseEntity<ApiResponse<String>> generateValidationErrorMessage(
            @RequestParam String fieldName,
            @RequestParam String value,
            @RequestParam String constraint) {
        
        String errorMessage = stringTemplateService.generateValidationErrorMessage(fieldName, value, constraint);
        
        return ResponseEntity.ok(ApiResponse.success(
            errorMessage,
            "Validation error message generated using text blocks"
        ));
    }
    
    /**
     * Get task statistics using enhanced collection APIs
     */
    @GetMapping("/task-statistics/{projectId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTaskStatistics(@PathVariable Long projectId) {
        Map<String, Object> stats = stringTemplateService.getTaskStatistics(projectId);
        
        return ResponseEntity.ok(ApiResponse.success(
            stats,
            "Task statistics retrieved using enhanced collection APIs"
        ));
    }
    
    /**
     * Generate task assignment email using text blocks
     */
    @GetMapping("/task-email")
    public ResponseEntity<ApiResponse<String>> generateTaskAssignmentEmail() {
        // Create sample data for demonstration
        User assignee = new User();
        assignee.setUsername("john.doe");
        assignee.setEmail("john.doe@example.com");
        
        Task task = new Task();
        task.setTitle("Complete Project Documentation");
        task.setDescription("Write comprehensive documentation for the new feature");
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDateTime.now().plusDays(7));
        task.setStatus(TaskStatus.TODO);
        
        User assigner = new User();
        assigner.setUsername("jane.smith");
        assigner.setEmail("jane.smith@example.com");
        
        String email = stringTemplateService.generateTaskAssignmentEmail(assignee, task, assigner);
        
        return ResponseEntity.ok(ApiResponse.success(
            email,
            "Task assignment email generated using text blocks"
        ));
    }
    
    /**
     * Generate task report using text blocks
     */
    @GetMapping("/task-report")
    public ResponseEntity<ApiResponse<String>> generateTaskReport() {
        // Create sample data for demonstration
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Implement User Authentication");
        task.setDescription("Add JWT-based authentication system");
        task.setStatus(TaskStatus.COMPLETED);
        task.setPriority(Priority.HIGH);
        task.setDueDate(LocalDateTime.now().minusDays(1));
        task.setCreatedAt(LocalDateTime.now().minusDays(10));
        task.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        // Create comments using empty list for demonstration
        List<Comment> comments = List.of();
        
        String report = stringTemplateService.formatTaskReport(task, comments);
        
        return ResponseEntity.ok(ApiResponse.success(
            report,
            "Task report generated using text blocks"
        ));
    }
    
    /**
     * Log task creation using text blocks
     */
    @PostMapping("/log-task-creation")
    public ResponseEntity<ApiResponse<String>> logTaskCreation() {
        // Create sample task for demonstration
        Task task = new Task();
        task.setId(123L);
        task.setTitle("New Feature Development");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDateTime.now().plusDays(14));
        task.setCreatedAt(LocalDateTime.now());
        
        stringTemplateService.logTaskCreation(task);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Task creation logged using text blocks",
            "Check logs for structured logging output"
        ));
    }
}
