package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status; // Should use: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority; // Should use: LOW, MEDIUM, HIGH, URGENT
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Missing from original - should be in database
    private ProjectDTO project;
    private UserDTO assignee;
    private List<CommentDTO> comments;
    
    // Constructors
    public TaskDTO() {}
    
    public TaskDTO(Long id, String title, String description, String status, 
                   String priority, LocalDateTime dueDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public ProjectDTO getProject() { return project; }
    public void setProject(ProjectDTO project) { this.project = project; }
    
    public UserDTO getAssignee() { return assignee; }
    public void setAssignee(UserDTO assignee) { this.assignee = assignee; }
    
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
}
