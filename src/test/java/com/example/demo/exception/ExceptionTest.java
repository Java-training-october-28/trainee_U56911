package com.example.demo.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exception classes
 * Tests exception creation and message formatting
 */
class ExceptionTest {
    
    @Test
    void resourceNotFoundException_ShouldCreateCorrectMessage() {
        // Test basic constructor
        ResourceNotFoundException ex1 = new ResourceNotFoundException("Test message");
        assertEquals("Test message", ex1.getMessage());
        
        // Test constructor with resource type and ID
        ResourceNotFoundException ex2 = new ResourceNotFoundException("User", 123L);
        assertEquals("User with id '123' not found", ex2.getMessage());
        
        // Test constructor with resource type, field, and value
        ResourceNotFoundException ex3 = new ResourceNotFoundException("User", "email", "test@example.com");
        assertEquals("User with email 'test@example.com' not found", ex3.getMessage());
    }
    
    @Test
    void resourceNotFoundException_ConvenienceMethods_ShouldWork() {
        ResourceNotFoundException userEx = ResourceNotFoundException.user(123L);
        assertEquals("User with id '123' not found", userEx.getMessage());
        
        ResourceNotFoundException projectEx = ResourceNotFoundException.project(456L);
        assertEquals("Project with id '456' not found", projectEx.getMessage());
        
        ResourceNotFoundException taskEx = ResourceNotFoundException.task(789L);
        assertEquals("Task with id '789' not found", taskEx.getMessage());
        
        ResourceNotFoundException commentEx = ResourceNotFoundException.comment(999L);
        assertEquals("Comment with id '999' not found", commentEx.getMessage());
    }
    
    @Test
    void validationException_ShouldHandleMultipleErrors() {
        List<String> errors = List.of("Field 'email' is required", "Field 'name' is too short");
        ValidationException ex = new ValidationException("Validation failed", errors);
        
        assertEquals("Validation failed", ex.getMessage());
        assertEquals(2, ex.getValidationErrors().size());
        assertTrue(ex.getValidationErrors().contains("Field 'email' is required"));
        assertTrue(ex.getValidationErrors().contains("Field 'name' is too short"));
    }
    
    @Test
    void validationException_ShouldHandleSingleError() {
        ValidationException ex = new ValidationException("Single validation error");
        
        assertEquals("Single validation error", ex.getMessage());
        assertNull(ex.getValidationErrors());
    }
    
    @Test
    void businessException_ShouldSupportErrorCodes() {
        BusinessException ex = new BusinessException("BUSINESS_001", "Business rule violated", "param1", "param2");
        
        assertEquals("Business rule violated", ex.getMessage());
        assertEquals("BUSINESS_001", ex.getErrorCode());
        assertArrayEquals(new Object[]{"param1", "param2"}, ex.getMessageArgs());
    }
    
    @Test
    void accessDeniedException_ShouldFormatMessage() {
        AccessDeniedException ex1 = new AccessDeniedException("Access denied");
        assertEquals("Access denied", ex1.getMessage());
        
        AccessDeniedException ex2 = new AccessDeniedException("users", "delete");
        assertEquals("Access denied for action 'delete' on resource 'users'", ex2.getMessage());
    }
    
    @Test
    void resourceAlreadyExistsException_ShouldFormatMessage() {
        ResourceAlreadyExistsException ex1 = new ResourceAlreadyExistsException("Resource exists");
        assertEquals("Resource exists", ex1.getMessage());
        
        ResourceAlreadyExistsException ex2 = new ResourceAlreadyExistsException("User", "email", "test@example.com");
        assertEquals("User with email 'test@example.com' already exists", ex2.getMessage());
    }
    
    @Test
    void resourceAlreadyExistsException_ConvenienceMethods_ShouldWork() {
        ResourceAlreadyExistsException emailEx = ResourceAlreadyExistsException.userEmail("test@example.com");
        assertEquals("User with email 'test@example.com' already exists", emailEx.getMessage());
        
        ResourceAlreadyExistsException nameEx = ResourceAlreadyExistsException.projectName("TestProject");
        assertEquals("Project with name 'TestProject' already exists", nameEx.getMessage());
    }
    
    @Test
    void baseException_ShouldSupportInheritance() {
        // Test that all custom exceptions extend BaseException
        assertTrue(new ResourceNotFoundException("test") instanceof BaseException);
        assertTrue(new ValidationException("test") instanceof BaseException);
        assertTrue(new BusinessException("test") instanceof BaseException);
        assertTrue(new AccessDeniedException("test") instanceof BaseException);
        assertTrue(new ResourceAlreadyExistsException("test") instanceof BaseException);
    }
    
    @Test
    void baseException_ShouldSupportCauseChaining() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("Test message", cause);
        
        assertEquals("Test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}