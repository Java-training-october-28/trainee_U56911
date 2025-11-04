package com.example.demo.exception;

/**
 * Exception thrown when a requested resource is not found
 * Typically results in HTTP 404 status
 */
public class ResourceNotFoundException extends BaseException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(String.format("%s with id '%s' not found", resourceType, identifier));
    }
    
    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super(String.format("%s with %s '%s' not found", resourceType, field, value));
    }
    
    // Convenience methods for common resources
    public static ResourceNotFoundException user(Long id) {
        return new ResourceNotFoundException("User", id);
    }
    
    public static ResourceNotFoundException project(Long id) {
        return new ResourceNotFoundException("Project", id);
    }
    
    public static ResourceNotFoundException task(Long id) {
        return new ResourceNotFoundException("Task", id);
    }
    
    public static ResourceNotFoundException comment(Long id) {
        return new ResourceNotFoundException("Comment", id);
    }
}