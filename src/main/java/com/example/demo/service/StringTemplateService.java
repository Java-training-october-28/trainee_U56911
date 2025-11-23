package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Service demonstrating Java 21 string templates and enhanced APIs
 */
@Service
public class StringTemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(StringTemplateService.class);
    private final TaskRepository taskRepository;
    
    public StringTemplateService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * Demonstrates text blocks for SQL-like queries
     */
    public String generateTaskSearchQuery(String searchTerm, TaskStatus status, TaskPriority priority) {
        // Using text blocks for SQL-like query construction
        StringBuilder query = new StringBuilder("""
            SELECT t.id, t.title, t.description, t.status, t.priority, t.due_date
            FROM tasks t
            WHERE 1=1
            """);
        
        if (searchTerm != null && !searchTerm.isBlank()) {
            query.append("AND (LOWER(t.title) LIKE LOWER('%").append(searchTerm).append("%') OR LOWER(t.description) LIKE LOWER('%").append(searchTerm).append("%'))\n");
        }
        if (status != null) {
            query.append("AND t.status = '").append(status).append("'\n");
        }
        if (priority != null) {
            query.append("AND t.priority = '").append(priority).append("'\n");
        }
        query.append("ORDER BY t.due_date ASC, t.priority DESC");
        
        String finalQuery = query.toString();
        logger.info("Generated SQL query: {}", finalQuery);
        return finalQuery;
    }
    
    /**
     * Demonstrates text blocks for logging messages
     */
    public void logTaskCreation(Task task) {
        // Using text blocks for structured logging
        String logMessage = String.format("""
            Task Created Successfully:
            - ID: %s
            - Title: %s
            - Status: %s
            - Priority: %s
            - Due Date: %s
            - Created At: %s
            """,
            task.getId(),
            task.getTitle(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "Not set",
            task.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        
        logger.info(logMessage);
    }
    
    /**
     * Demonstrates text blocks for email notifications
     */
    public String generateTaskAssignmentEmail(User assignee, Task task, User assigner) {
        return String.format("""
            Subject: New Task Assignment - %s
            
            Dear %s,
            
            You have been assigned a new task:
            
            Task Details:
            - Title: %s
            - Description: %s
            - Priority: %s
            - Due Date: %s
            - Status: %s
            
            Assigned by: %s (%s)
            
            Please log in to the Task Management System to view and work on this task.
            
            Best regards,
            Task Management System
            """,
            task.getTitle(),
            assignee.getUsername(),
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "No description provided",
            task.getPriority(),
            task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) : "Not specified",
            task.getStatus(),
            assigner.getUsername(),
            assigner.getEmail()
        );
    }
    
    /**
     * Demonstrates text blocks for error messages
     */
    public String generateValidationErrorMessage(String fieldName, String value, String constraint) {
        return String.format("""
            Validation Error:
            - Field: %s
            - Value: '%s'
            - Constraint: %s
            - Timestamp: %s
            """,
            fieldName,
            value,
            constraint,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
    
    /**
     * Demonstrates enhanced collection APIs with sequenced collections
     */
    public Map<String, Object> getTaskStatistics(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        
        // Using enhanced collection APIs
        var stats = new LinkedHashMap<String, Object>();
        
        // Sequenced collection operations
        stats.putFirst("projectId", projectId);
        stats.put("totalTasks", tasks.size());
        stats.put("completedTasks", tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count());
        stats.put("inProgressTasks", tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .count());
        stats.put("overdueTasks", tasks.stream()
            .filter(task -> task.getDueDate() != null && 
                           task.getDueDate().isBefore(LocalDateTime.now()) &&
                           task.getStatus() != TaskStatus.COMPLETED)
            .count());
        
        // Enhanced collection methods
        if (!stats.isEmpty()) {
            var firstEntry = stats.firstEntry();
            var lastEntry = stats.lastEntry();
            logger.info("First entry: {}={}, Last entry: {}={}", 
                       firstEntry.getKey(), firstEntry.getValue(),
                       lastEntry.getKey(), lastEntry.getValue());
        }
        
        return stats;
    }
    
    /**
     * Demonstrates text blocks for API responses
     */
    public String generateApiResponse(String operation, boolean success, Object data) {
        return String.format("""
            {
                "operation": "%s",
                "success": %s,
                "timestamp": "%s",
                "data": %s
            }
            """,
            operation,
            success,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            data != null ? data.toString() : "null"
        );
    }
    
    /**
     * Demonstrates text blocks for complex object formatting
     */
    public String formatTaskReport(Task task, List<Comment> comments) {
        return String.format("""
            TASK REPORT
            ===========
            
            Task Information:
            - ID: %s
            - Title: %s
            - Description: %s
            - Status: %s
            - Priority: %s
            - Due Date: %s
            - Created: %s
            - Updated: %s
            
            Comments (%s):
            %s
            
            Summary:
            - Total comments: %s
            - Last updated: %s
            - Current status: %s
            """,
            task.getId(),
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "N/A",
            task.getStatus(),
            task.getPriority(),
            task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Not set",
            task.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            task.getUpdatedAt() != null ? task.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Never",
            comments.size(),
            formatComments(comments),
            comments.size(),
            task.getUpdatedAt() != null ? task.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Never",
            task.getStatus()
        );
    }
    
    /**
     * Helper method using text blocks for comment formatting
     */
    private String formatComments(List<Comment> comments) {
        if (comments.isEmpty()) {
            return "No comments available.";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            sb.append(String.format("""
            Comment #%s:
              - Author: %s
              - Date: %s
              - Content: %s
              
            """,
                i + 1,
                comment.getUser() != null ? comment.getUser().getUsername() : "Unknown",
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                comment.getContent()
            ));
        }
        return sb.toString();
    }
    
    /**
     * Demonstrates enhanced string methods
     */
    public void demonstrateEnhancedStringMethods() {
        String multilineText = """
            This is a multiline text block
            that demonstrates Java 21 text blocks
            with proper indentation handling.
            """;
        
        // Enhanced string methods
        String transformed = multilineText
            .transform(s -> s.toUpperCase())
            .transform(s -> s.replace("JAVA", "MODERN JAVA"));
        
        logger.info("Transformed text: {}", transformed);
        
        // String indentation
        String indented = multilineText.indent(4);
        logger.info("Indented text:\n{}", indented);
        
        // String stripping
        String withWhitespace = "   Hello World   ";
        logger.info("Stripped: '{}'", withWhitespace.strip());
        logger.info("Leading stripped: '{}'", withWhitespace.stripLeading());
        logger.info("Trailing stripped: '{}'", withWhitespace.stripTrailing());
    }
}
