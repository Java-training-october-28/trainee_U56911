package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * Tests service layer exception handling and business logic
 */
@ExtendWith(MockitoExtension.class)
class UserServiceExceptionTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("johndoe");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.USER);
    }
    
    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        User result = userService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }
    
    @Test
    void findById_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findById(999L)
        );
        
        assertTrue(exception.getMessage().contains("User with id '999' not found"));
        verify(userRepository).findById(999L);
    }
    
    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When
        User result = userService.findByEmail("test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }
    
    @Test
    void findByEmail_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findByEmail("nonexistent@example.com")
        );
        
        assertTrue(exception.getMessage().contains("User with email 'nonexistent@example.com' not found"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void deleteById_WhenUserExists_ShouldDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        
        // When
        userService.deleteById(1L);
        
        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    void deleteById_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.existsById(anyLong())).thenReturn(false);
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.deleteById(999L)
        );
        
        assertTrue(exception.getMessage().contains("User with id '999' not found"));
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }
    
    @Test
    void save_ShouldCallRepository() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User result = userService.save(testUser);
        
        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).save(testUser);
    }
    
    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<User> result = userService.findAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }
    
    @Test
    void existsByEmail_ShouldReturnCorrectValue() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);
        
        // When & Then
        assertTrue(userService.existsByEmail("test@example.com"));
        assertFalse(userService.existsByEmail("nonexistent@example.com"));
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
    
    @Test
    void findByRole_ShouldReturnUsersWithRole() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRole(Role.USER)).thenReturn(users);
        
        // When
        List<User> result = userService.findByRole(Role.USER);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Role.USER, result.get(0).getRole());
        verify(userRepository).findByRole(Role.USER);
    }
    
    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        when(userRepository.count()).thenReturn(5L);
        
        // When
        long result = userService.count();
        
        // Then
        assertEquals(5L, result);
        verify(userRepository).count();
    }
}