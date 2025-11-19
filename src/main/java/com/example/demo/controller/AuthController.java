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
    
    /**
     * POST /api/auth/logout - Logout user
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout user by revoking refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody AuthLogoutDTO logoutDTO) {
        authService.logout(logoutDTO.getRefreshToken());
        ApiResponse<Void> response = ApiResponse.success(null, "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/auth/logout-all - Logout from all devices
     */
    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Revoke all refresh tokens for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> logoutFromAllDevices(@RequestHeader("Authorization") String authHeader) {
        // Extract user ID from access token
        String accessToken = authHeader.substring(7); // Remove "Bearer " prefix
        Long userId = authService.getUserIdFromToken(accessToken);
        
        authService.logoutAllDevices(userId);
        ApiResponse<Void> response = ApiResponse.success(null, "Logged out from all devices successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/auth/validate - Validate access token
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate access token", description = "Validate if the provided access token is valid and return user information")
    public ResponseEntity<ApiResponse<AuthValidationDTO>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = authHeader.substring(7); // Remove "Bearer " prefix
            
            if (authService.validateAccessToken(accessToken)) {
                var user = authService.getUserFromToken(accessToken);
                if (user != null) {
                    AuthValidationDTO validation = new AuthValidationDTO(
                        true, user.getEmail(), user.getRole().name(), user.getId(), "Token is valid"
                    );
                    ApiResponse<AuthValidationDTO> response = ApiResponse.success(validation, "Token validation successful");
                    return ResponseEntity.ok(response);
                }
            }
            
            AuthValidationDTO validation = new AuthValidationDTO(false, "Invalid token");
            ApiResponse<AuthValidationDTO> response = ApiResponse.error("Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (Exception e) {
            AuthValidationDTO validation = new AuthValidationDTO(false, "Token validation failed: " + e.getMessage());
            ApiResponse<AuthValidationDTO> response = ApiResponse.error("Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
