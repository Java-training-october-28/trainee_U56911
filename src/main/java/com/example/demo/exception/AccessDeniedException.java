package com.example.demo.exception;

/**
 * Exception thrown when access to a resource is denied
 * Typically results in HTTP 403 status
 */
public class AccessDeniedException extends BaseException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AccessDeniedException(String resource, String action) {
        super(String.format("Access denied for action '%s' on resource '%s'", action, resource));
    }
}