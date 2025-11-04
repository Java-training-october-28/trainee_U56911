package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for validation error details
 * Contains field-specific validation error information
 */
public class ValidationErrorDetail {
    
    @JsonProperty("field")
    private String field;
    
    @JsonProperty("rejectedValue")
    private Object rejectedValue;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("code")
    private String code;
    
    public ValidationErrorDetail() {}
    
    public ValidationErrorDetail(String field, Object rejectedValue, String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }
    
    public ValidationErrorDetail(String field, Object rejectedValue, String message, String code) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
        this.code = code;
    }
    
    // Getters and Setters
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}