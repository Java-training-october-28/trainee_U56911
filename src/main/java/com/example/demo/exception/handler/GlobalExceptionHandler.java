package com.example.demo.exception.handler;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ErrorResponse;
import com.example.demo.dto.response.ValidationErrorDetail;
import com.example.demo.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the entire application
 * Provides centralized exception handling with consistent response format
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ResourceNotFoundException - HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.notFound(ex.getMessage());
        errorResponse.setPath(getPath(request));
        errorResponse.setMethod(getMethod(request));
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle ValidationException - HTTP 400
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        List<String> errors = ex.getValidationErrors() != null ? 
            ex.getValidationErrors() : List.of(ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Validation failed", errors, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle BusinessException - HTTP 409
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        logger.warn("Business rule violation: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle AccessDeniedException - HTTP 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        logger.warn("Access denied: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle ResourceAlreadyExistsException - HTTP 409
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, WebRequest request) {
        
        logger.warn("Resource already exists: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle MethodArgumentNotValidException - Bean Validation errors - HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDetail>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        List<ValidationErrorDetail> validationErrors = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ValidationErrorDetail detail = new ValidationErrorDetail(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage(),
                error.getCode()
            );
            validationErrors.add(detail);
            errorMessages.add(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
        }
        
        ApiResponse<List<ValidationErrorDetail>> response = ApiResponse.error(
            "Validation failed", errorMessages, getPath(request));
        response.setData(validationErrors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ConstraintViolationException - Method parameter validation - HTTP 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        List<String> errors = ex.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Constraint violation", errors, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle MethodArgumentTypeMismatchException - Type conversion errors - HTTP 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Type mismatch error: {}", ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
            ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        ApiResponse<Object> response = ApiResponse.error(message, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle HttpMessageNotReadableException - Malformed JSON - HTTP 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "Malformed JSON request", getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle HttpRequestMethodNotSupportedException - HTTP 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        logger.warn("Method not supported: {}", ex.getMessage());
        
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
            ex.getMethod(), String.join(", ", ex.getSupportedMethods()));
        
        ApiResponse<Object> response = ApiResponse.error(message, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    /**
     * Handle NoHandlerFoundException - HTTP 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        
        logger.warn("No handler found: {}", ex.getMessage());
        
        String message = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        
        ApiResponse<Object> response = ApiResponse.error(message, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle DataIntegrityViolationException - Database constraint violations - HTTP 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        logger.warn("Data integrity violation: {}", ex.getMessage());
        
        String message = "Data integrity violation. The operation conflicts with existing data constraints.";
        
        // Try to extract more specific information from the exception
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("duplicate key")) {
                message = "A record with the same unique field already exists.";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "The operation violates a foreign key constraint.";
            }
        }
        
        ApiResponse<Object> response = ApiResponse.error(message, getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle all other exceptions - HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        ApiResponse<Object> response = ApiResponse.error(
            "An unexpected error occurred. Please try again later.", getPath(request));
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Extract path from WebRequest
     */
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    /**
     * Extract HTTP method from WebRequest
     */
    private String getMethod(WebRequest request) {
        // This is a simplified implementation
        // In a real application, you might want to use HttpServletRequest directly
        return "HTTP";
    }
}