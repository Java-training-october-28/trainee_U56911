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

@Service
public class AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private DatabaseTokenStoreService databaseTokenStoreService;
    
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
        
        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        
        // Store refresh token in database
        databaseTokenStoreService.storeRefreshToken(savedUser.getId(), refreshToken);
        
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
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Store refresh token in database
        databaseTokenStoreService.storeRefreshToken(user.getId(), refreshToken);
        
        return new AuthResponseDTO(accessToken, refreshToken, userDTO);
    }
    
    /**
     * Refresh access token
     */
    public AuthResponseDTO refreshToken(AuthRefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();
        
        // Validate refresh token and get user ID from database
        Long userId = databaseTokenStoreService.validateRefreshToken(refreshToken);
        if (userId == null) {
            throw new BusinessException("Invalid or expired refresh token");
        }
        
        // Get user details
        User user = userService.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        // Validate JWT refresh token structure (optional double-check)
        if (!jwtService.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token format");
        }
        
        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user);
        
        // Optionally generate new refresh token (rotate refresh tokens)
        String newRefreshToken = jwtService.generateRefreshToken(user);
        
        // Remove old refresh token and store new one
        databaseTokenStoreService.revokeRefreshToken(refreshToken);
        databaseTokenStoreService.storeRefreshToken(userId, newRefreshToken);
        
        UserDTO userDTO = userMapper.toDTO(user);
        return new AuthResponseDTO(newAccessToken, newRefreshToken, userDTO);
    }
    
    /**
     * Logout user - revoke refresh token
     */
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            databaseTokenStoreService.revokeRefreshToken(refreshToken);
        }
    }
    
    /**
     * Logout user from all devices - revoke all refresh tokens
     */
    public void logoutFromAllDevices(Long userId) {
        databaseTokenStoreService.revokeAllRefreshTokensForUser(userId);
    }
    
    /**
     * Validate access token
     */
    public boolean validateAccessToken(String accessToken) {
        return jwtService.validateToken(accessToken);
    }
    
    /**
     * Get user details from access token
     */
    public User getUserFromAccessToken(String accessToken) {
        if (!jwtService.validateToken(accessToken)) {
            return null;
        }
        
        Long userId = jwtService.getUserIdFromToken(accessToken);
        return userService.findById(userId);
    }
}
