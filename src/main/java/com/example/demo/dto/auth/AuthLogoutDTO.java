package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthLogoutDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Constructors
    public AuthLogoutDTO() {}
    
    public AuthLogoutDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}