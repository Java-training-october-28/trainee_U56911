package com.example.demo.service;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.Role;

import java.util.List;

public interface UserServiceInterface {
    
    /**
     * Create a new user
     */
    UserDTO createUser(UserCreateDTO createDTO);
    
    /**
     * Get user by ID
     */
    UserDTO getUserById(Long id);
    
    /**
     * Get user by username
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * Get user by email
     */
    UserDTO getUserByEmail(String email);
    
    /**
     * Update user information
     */
    UserDTO updateUser(Long id, UserUpdateDTO updateDTO);
    
    /**
     * Delete user by ID
     */
    void deleteUser(Long id);
    
    /**
     * Get all users
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Get users by role
     */
    List<UserDTO> getUsersByRole(Role role);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Get active users
     */
    List<UserDTO> getActiveUsers();
    
    /**
     * Count all users
     */
    long countUsers();
}
