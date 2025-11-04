package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for User entity operations
 * Contains business logic for user management
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Save or update a user
     */
    public User save(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
    }
    
    /**
     * Find all users
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    /**
     * Find users by role
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Delete user by ID
     */
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.user(id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Find active users
     */
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    /**
     * Count all users
     */
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }
}