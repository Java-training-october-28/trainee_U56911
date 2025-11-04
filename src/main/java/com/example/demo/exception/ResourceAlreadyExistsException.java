package com.example.demo.exception;

/**
 * Exception thrown when a resource already exists
 * Typically results in HTTP 409 status
 */
public class ResourceAlreadyExistsException extends BaseException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceAlreadyExistsException(String resourceType, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", resourceType, field, value));
    }
    
    // Convenience methods
    public static ResourceAlreadyExistsException userEmail(String email) {
        return new ResourceAlreadyExistsException("User", "email", email);
    }
    
    public static ResourceAlreadyExistsException projectName(String name) {
        return new ResourceAlreadyExistsException("Project", "name", name);
    }
}