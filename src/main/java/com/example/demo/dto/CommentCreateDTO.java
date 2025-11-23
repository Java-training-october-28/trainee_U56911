package com.example.demo.dto;

import jakarta.validation.constraints.*;

public record CommentCreateDTO(
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    String content,
    
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotNull(message = "Task ID is required")
    Long taskId
) {
    // Compact constructor for validation
    public CommentCreateDTO {
        if (content != null && content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be blank");
        }
    }
}
