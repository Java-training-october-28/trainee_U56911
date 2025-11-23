package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.TaskPriority;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record TaskCreateDTO(
    @NotBlank(message = "Task title is required")
    @Size(min = 3, max = 200, message = "Task title must be between 3 and 200 characters")
    String title,
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,
    
    @NotNull(message = "Status is required")
    TaskStatus status,
    
    @NotNull(message = "Priority is required")
    TaskPriority priority,
    
    LocalDateTime dueDate,
    
    @NotNull(message = "Project ID is required")
    Long projectId,
    
    Long assigneeId
) {
    // Compact constructor for validation
    public TaskCreateDTO {
        if (title != null && title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }
    }
}
