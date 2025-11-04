package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class CommentCreateDTO {
    
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment content must be between 1 and 1000 characters")
    private String content;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Task ID is required")
    private Long taskId;
    
    // Constructors
    public CommentCreateDTO() {}
    
    public CommentCreateDTO(String content, Long userId, Long taskId) {
        this.content = content;
        this.userId = userId;
        this.taskId = taskId;
    }
    
    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
}
