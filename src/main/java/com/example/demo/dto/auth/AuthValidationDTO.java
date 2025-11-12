package com.example.demo.dto.auth;

public class AuthValidationDTO {
    
    private boolean valid;
    private String username;
    private String role;
    private Long userId;
    private String message;
    
    // Constructors
    public AuthValidationDTO() {}
    
    public AuthValidationDTO(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public AuthValidationDTO(boolean valid, String username, String role, Long userId, String message) {
        this.valid = valid;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}