package com.example.demo.dto;

import com.example.demo.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private List<ProjectDTO> ownedProjects;
    private List<TaskDTO> assignedTasks;
    private List<CommentDTO> comments;
    
    // Constructors
    public UserDTO() {}
    
    public UserDTO(Long id, String username, String email, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<ProjectDTO> getOwnedProjects() { return ownedProjects; }
    public void setOwnedProjects(List<ProjectDTO> ownedProjects) { this.ownedProjects = ownedProjects; }
    
    public List<TaskDTO> getAssignedTasks() { return assignedTasks; }
    public void setAssignedTasks(List<TaskDTO> assignedTasks) { this.assignedTasks = assignedTasks; }
    
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
}
