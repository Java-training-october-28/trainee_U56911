package com.example.demo.service;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User entity operations
 * Contains business logic for user management
 */
@Service
@Transactional
public class UserService implements UserServiceInterface {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Create a new user with validation
     */
    @Override
    public UserDTO createUser(UserCreateDTO createDTO) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(createDTO.email())) {
            throw ResourceAlreadyExistsException.userEmail(createDTO.email());
        }
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(createDTO.username())) {
            throw ResourceAlreadyExistsException.userUsername(createDTO.username());
        }
        
        // Create user entity
        User user = new User();
        user.setUsername(createDTO.username());
        user.setEmail(createDTO.email());
        user.setPassword(passwordEncoder.encode(createDTO.password()));
        user.setRole(createDTO.role());
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Convert to DTO and return
        return userMapper.toDTO(savedUser);
    }
    
    /**
     * Get user by ID
     */
    @Override
    @Cacheable(value = "user", key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        return userMapper.toDTO(user);
    }
    
    /**
     * Get user by username
     */
    @Override
    @Cacheable(value = "user", key = "'username_' + #username")
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toDTO(user);
    }
    
    /**
     * Get user by email
     */
    @Override
    @Cacheable(value = "user", key = "'email_' + #email")
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toDTO(user);
    }
    
    /**
     * Update user information
     */
    @Override
    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        
        // Update fields if provided
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().isBlank()) {
            // Validate username uniqueness if changed
            if (!existingUser.getUsername().equals(updateDTO.getUsername()) && 
                userRepository.existsByUsername(updateDTO.getUsername())) {
                throw ResourceAlreadyExistsException.userUsername(updateDTO.getUsername());
            }
            existingUser.setUsername(updateDTO.getUsername());
        }
        
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
            // Validate email uniqueness if changed
            if (!existingUser.getEmail().equals(updateDTO.getEmail()) && 
                userRepository.existsByEmail(updateDTO.getEmail())) {
                throw ResourceAlreadyExistsException.userEmail(updateDTO.getEmail());
            }
            existingUser.setEmail(updateDTO.getEmail());
        }
        
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        if (updateDTO.getRole() != null) {
            existingUser.setRole(updateDTO.getRole());
        }
        
        // Save updated user
        User updatedUser = userRepository.save(existingUser);
        
        // Convert to DTO and return
        return userMapper.toDTO(updatedUser);
    }
    
    /**
     * Delete user by ID
     */
    @Override
    @CacheEvict(value = "user", key = "#id") // Evict the specific user by ID
    // Evict all lists of users, as they are now stale.
    // Using @Caching to group multiple cache annotations.
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.user(id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Get all users
     */
    @Override
    @Cacheable(value = "users", key = "'all'")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get users by role
     */
    @Override
    @Cacheable(value = "users", key = "'role_' + #role")
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if email exists
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Check if username exists
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Get active users
     */
    @Override
    @Cacheable(value = "users", key = "'active'")
    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        return userRepository.findActiveUsers()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Count all users
     */
    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }
    
    // --- Legacy methods for backward compatibility ---
    
    /**
     * Save or update a user (legacy method)
     */
    public User save(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Find user by ID (legacy method)
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
    }
    
    /**
     * Find all users (legacy method)
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Find user by email (legacy method)
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    /**
     * Find users by role (legacy method)
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Delete user by ID (legacy method)
     */
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.user(id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Find active users (legacy method)
     */
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    /**
     * Count all users (legacy method)
     */
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }
}
