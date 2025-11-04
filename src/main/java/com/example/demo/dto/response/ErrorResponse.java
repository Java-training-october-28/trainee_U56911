package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response DTO
 * Used for consistent error reporting across the application
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @JsonProperty("error")
    private boolean error;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("errorCode")
    private String errorCode;
    
    @JsonProperty("details")
    private List<String> details;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("status")
    private int status;
    
    public ErrorResponse() {
        this.error = true;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String message, int status) {
        this();
        this.message = message;
        this.status = status;
    }
    
    public ErrorResponse(String message, String errorCode, int status) {
        this();
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }
    
    public ErrorResponse(String message, List<String> details, int status) {
        this();
        this.message = message;
        this.details = details;
        this.status = status;
    }
    
    public ErrorResponse(String message, String errorCode, List<String> details, int status) {
        this();
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
        this.status = status;
    }
    
    // Static factory methods
    public static ErrorResponse badRequest(String message) {
        return new ErrorResponse(message, 400);
    }
    
    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(message, 404);
    }
    
    public static ErrorResponse forbidden(String message) {
        return new ErrorResponse(message, 403);
    }
    
    public static ErrorResponse conflict(String message) {
        return new ErrorResponse(message, 409);
    }
    
    public static ErrorResponse internalServerError(String message) {
        return new ErrorResponse(message, 500);
    }
    
    public static ErrorResponse validationError(String message, List<String> details) {
        return new ErrorResponse(message, details, 400);
    }
    
    // Getters and Setters
    public boolean isError() {
        return error;
    }
    
    public void setError(boolean error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public List<String> getDetails() {
        return details;
    }
    
    public void setDetails(List<String> details) {
        this.details = details;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}