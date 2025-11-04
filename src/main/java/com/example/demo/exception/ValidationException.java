package com.example.demo.exception;

import java.util.List;

/**
 * Exception thrown when validation fails
 * Typically results in HTTP 400 status
 */
public class ValidationException extends BaseException {
    
    private final List<String> validationErrors;
    
    public ValidationException(String message) {
        super(message);
        this.validationErrors = null;
    }
    
    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = null;
    }
    
    public ValidationException(List<String> validationErrors) {
        super("Validation failed");
        this.validationErrors = validationErrors;
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}