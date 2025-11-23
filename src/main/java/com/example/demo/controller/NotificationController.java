package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.NotificationPriority;
import com.example.demo.entity.NotificationType;
import com.example.demo.entity.TaskNotification;
import com.example.demo.entity.ProjectNotification;
import com.example.demo.entity.SystemNotification;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating Java 21 pattern matching and sealed classes
 * Exposes endpoints that use modern Java features
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Process different notification types using pattern matching
     * Demonstrates sealed classes and switch expressions
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<String>> processNotification(@RequestBody NotificationType notification) {
        String result = notificationService.processNotification(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Notification processed successfully",
            result
        ));
    }
    
    /**
     * Get notification category using pattern matching in switch expressions
     */
    @PostMapping("/category")
    public ResponseEntity<ApiResponse<String>> getNotificationCategory(@RequestBody NotificationType notification) {
        String category = notificationService.getNotificationCategory(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Notification category retrieved",
            category
        ));
    }
    
    /**
     * Filter high-priority notifications using pattern matching
     */
    @GetMapping("/high-priority")
    public ResponseEntity<ApiResponse<List<String>>> getHighPriorityMessages() {
        // Create sample notifications for demonstration
        List<NotificationType> notifications = List.of(
            new TaskNotification("Task overdue - Complete task ASAP", 1L, "Urgent Task", NotificationPriority.HIGH),
            new ProjectNotification("Project deadline approaching", 2L, "Project Alpha", NotificationPriority.MEDIUM),
            new SystemNotification("System maintenance scheduled", "Infrastructure", NotificationPriority.HIGH),
            new TaskNotification("New task assigned", 3L, "Regular Task", NotificationPriority.LOW)
        );
        
        List<String> highPriority = notificationService.getHighPriorityMessages(notifications);
        
        return ResponseEntity.ok(ApiResponse.success(
            highPriority,
            "High priority notifications retrieved"
        ));
    }
    
    /**
     * Check if notification is high priority using pattern matching
     */
    @PostMapping("/check-priority")
    public ResponseEntity<ApiResponse<Boolean>> isHighPriorityNotification(@RequestBody NotificationType notification) {
        boolean isHighPriority = notificationService.isHighPriorityNotification(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            isHighPriority,
            "Priority check completed"
        ));
    }
    
    /**
     * Get notification action using complex pattern matching
     */
    @PostMapping("/action")
    public ResponseEntity<ApiResponse<String>> getNotificationAction(@RequestBody NotificationType notification) {
        String action = notificationService.getNotificationAction(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Notification action determined",
            action
        ));
    }
    
    /**
     * Create and process a task notification
     */
    @PostMapping("/task")
    public ResponseEntity<ApiResponse<String>> createTaskNotification(
            @RequestParam String message,
            @RequestParam Long taskId,
            @RequestParam String taskTitle,
            @RequestParam NotificationPriority priority) {
        
        TaskNotification notification = new TaskNotification(message, taskId, taskTitle, priority);
        String result = notificationService.processNotification(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Task notification created and processed",
            result
        ));
    }
    
    /**
     * Create and process a project notification
     */
    @PostMapping("/project")
    public ResponseEntity<ApiResponse<String>> createProjectNotification(
            @RequestParam String message,
            @RequestParam Long projectId,
            @RequestParam String projectName,
            @RequestParam NotificationPriority priority) {
        
        ProjectNotification notification = new ProjectNotification(message, projectId, projectName, priority);
        String result = notificationService.processNotification(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "Project notification created and processed",
            result
        ));
    }
    
    /**
     * Create and process a system notification
     */
    @PostMapping("/system")
    public ResponseEntity<ApiResponse<String>> createSystemNotification(
            @RequestParam String message,
            @RequestParam String systemComponent,
            @RequestParam NotificationPriority priority) {
        
        SystemNotification notification = new SystemNotification(message, systemComponent, priority);
        String result = notificationService.processNotification(notification);
        
        return ResponseEntity.ok(ApiResponse.success(
            "System notification created and processed",
            result
        ));
    }
}
