package com.example.demo.controller;

import com.example.demo.dto.auth.*;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * POST /api/auth/register - Register a new user
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody AuthRegisterDTO registerDTO) {
        AuthResponseDTO authResponse = authService.register(registerDTO);
        ApiResponse<AuthResponseDTO> response = ApiResponse.success(authResponse, "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * POST /api/auth/login - Login user
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with email and password")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody AuthLoginDTO loginDTO) {
        AuthResponseDTO authResponse = authService.login(loginDTO);
        ApiResponse<AuthResponseDTO> response = ApiResponse.success(authResponse, "Login successful");
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/auth/refresh - Refresh access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(@Valid @RequestBody AuthRefreshDTO refreshDTO) {
        AuthResponseDTO authResponse = authService.refreshToken(refreshDTO);
        ApiResponse<AuthResponseDTO> response = ApiResponse.success(authResponse, "Token refreshed successfully");
        return ResponseEntity.ok(response);
    }
}
