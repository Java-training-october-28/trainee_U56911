package com.example.demo.exception;

/**
 * Base class for all business-related exceptions
 * Provides common functionality for custom exceptions
 */
public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] messageArgs;
    
    protected BaseException(String message) {
        super(message);
        this.errorCode = null;
        this.messageArgs = null;
    }
    
    protected BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.messageArgs = null;
    }
    
    protected BaseException(String errorCode, String message, Object... messageArgs) {
        super(message);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }
    
    protected BaseException(String errorCode, String message, Throwable cause, Object... messageArgs) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getMessageArgs() {
        return messageArgs;
    }
}