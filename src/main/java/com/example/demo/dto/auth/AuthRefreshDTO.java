package com.example.demo.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthRefreshDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Constructors
    public AuthRefreshDTO() {}
    
    public AuthRefreshDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
