package com.example.demo.dto;

import com.example.demo.entity.ProjectStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ProjectCreateDTO(
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    LocalDateTime startDate,
    
    LocalDateTime endDate,
    
    ProjectStatus status,
    
    @NotNull(message = "Owner ID is required")
    Long ownerId
) {
    // Compact constructor for validation
    public ProjectCreateDTO {
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be blank");
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
