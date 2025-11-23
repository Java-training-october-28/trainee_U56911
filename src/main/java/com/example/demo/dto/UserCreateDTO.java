package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.*;

public record UserCreateDTO(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,
    
    Role role
) {
    // Compact constructor for validation
    public UserCreateDTO {
        if (username != null && username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (email != null && !email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
    }
}
