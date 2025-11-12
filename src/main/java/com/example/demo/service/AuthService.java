package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user
     */
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) {
        // Check if user already exists
        if (userService.existsByEmail(registerDTO.getEmail())) {
            throw ResourceAlreadyExistsException.userEmail(registerDTO.getEmail());
        }
        
        // Create user entity
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole());
        
        // Save user
        User savedUser = userService.save(user);
        UserDTO userDTO = userMapper.toDTO(savedUser);
        
        // Generate tokens (simple implementation)
        String accessToken = generateAccessToken(savedUser);
        String refreshToken = generateRefreshToken();
        
        return new AuthResponseDTO(accessToken, refreshToken, userDTO);
    }
    
    /**
     * Login user
     */
    public AuthResponseDTO login(AuthLoginDTO loginDTO) {
        // Find user by email
        User user = userService.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new BusinessException("Invalid email or password");
        }
        
        // Check password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }
        
        UserDTO userDTO = userMapper.toDTO(user);
        
        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken();
        
        return new AuthResponseDTO(accessToken, refreshToken, userDTO);
    }
    
    /**
     * Refresh access token
     */
    public AuthResponseDTO refreshToken(AuthRefreshDTO refreshDTO) {
        // In a real implementation, you would validate the refresh token
        // For now, we'll return a new access token
        String accessToken = generateAccessToken(null);
        String newRefreshToken = generateRefreshToken();
        
        return new AuthResponseDTO(accessToken, newRefreshToken, null);
    }
    
    /**
     * Generate access token (simplified implementation)
     */
    private String generateAccessToken(User user) {
        // In a real implementation, you would use JWT
        // For now, return a simple token
        return "access_token_" + UUID.randomUUID().toString();
    }
    
    /**
     * Generate refresh token (simplified implementation)
     */
    private String generateRefreshToken() {
        return "refresh_token_" + UUID.randomUUID().toString();
    }
}
