package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.TaskPriority;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TaskCreateDTO {
    
    @NotBlank(message = "Task title is required")
    @Size(min = 3, max = 200, message = "Task title must be between 3 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Status is required")
    private TaskStatus status; // Use enum instead of String
    
    @NotNull(message = "Priority is required")
    private TaskPriority priority; // Use enum instead of String
    
    private LocalDateTime dueDate;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    private Long assigneeId;
    
    // Constructors
    public TaskCreateDTO() {}
    
    public TaskCreateDTO(String title, String description, TaskStatus status, TaskPriority priority, 
                        LocalDateTime dueDate, Long projectId, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
}
