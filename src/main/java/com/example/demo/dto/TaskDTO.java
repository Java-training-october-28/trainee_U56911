package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TaskDTO(Long id, String title, String description, String status, String priority, LocalDateTime dueDate, LocalDateTime createdAt, LocalDateTime updatedAt, ProjectDTO project, UserDTO assignee, List<CommentDTO> comments) {}
