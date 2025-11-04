package com.example.demo.exception;

/**
 * Exception thrown when a business rule is violated
 * Typically results in HTTP 409 status
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BusinessException(String errorCode, String message, Object... messageArgs) {
        super(errorCode, message, messageArgs);
    }
    
    public BusinessException(String errorCode, String message, Throwable cause, Object... messageArgs) {
        super(errorCode, message, cause, messageArgs);
    }
}