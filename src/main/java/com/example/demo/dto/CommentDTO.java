package com.example.demo.dto;

import java.time.LocalDateTime;

public record CommentDTO(Long id, String content, LocalDateTime createdAt, UserDTO user, TaskDTO task) {}
