package com.example.demo.dto;

import com.example.demo.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

public record UserDTO(
    Long id,
    String username,
    String email,
    Role role,
    LocalDateTime createdAt,
    List<ProjectDTO> ownedProjects,
    List<TaskDTO> assignedTasks,
    List<CommentDTO> comments
) {}
