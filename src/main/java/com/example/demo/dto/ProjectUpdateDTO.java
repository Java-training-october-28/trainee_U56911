package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ProjectUpdateDTO {
    
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String status;
    
    private Long ownerId;
    
    // Constructors
    public ProjectUpdateDTO() {}
    
    public ProjectUpdateDTO(String name, String description, LocalDateTime startDate, 
                           LocalDateTime endDate, String status, Long ownerId) {
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
