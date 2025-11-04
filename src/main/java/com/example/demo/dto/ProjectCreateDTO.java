package com.example.demo.dto;

import com.example.demo.entity.ProjectStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ProjectCreateDTO {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @NotNull(message = "Status is required")
    private ProjectStatus status; // Use enum instead of String
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    // Constructors
    public ProjectCreateDTO() {}
    
    public ProjectCreateDTO(String name, String description, LocalDateTime startDate, 
                           LocalDateTime endDate, ProjectStatus status, Long ownerId) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.ownerId = ownerId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
