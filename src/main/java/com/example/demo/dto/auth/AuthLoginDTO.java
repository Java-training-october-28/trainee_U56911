package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginDTO {
    
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructors
    public AuthLoginDTO() {}
    
    public AuthLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
