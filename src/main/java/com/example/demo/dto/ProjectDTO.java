package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDTO(Long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate, String status, LocalDateTime createdAt, LocalDateTime updatedAt, UserDTO owner, List<TaskDTO> tasks) {}
