package com.example.demo.exception.handler;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.exception.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Test controller for testing exception handling scenarios
 * Used only in test environment to trigger specific exceptions
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/resource-not-found")
    public void throwResourceNotFoundException() {
        throw ResourceNotFoundException.user(123L);
    }
    
    @GetMapping("/validation-error")
    public void throwValidationException() {
        List<String> errors = List.of("Field 'email' is required", "Field 'name' is too short");
        throw new ValidationException("Validation failed", errors);
    }
    
    @GetMapping("/business-error")
    public void throwBusinessException() {
        throw new BusinessException("A business rule has been violated");
    }
    
    @GetMapping("/access-denied")
    public void throwAccessDeniedException() {
        throw new AccessDeniedException("Access denied for this operation");
    }
    
    @GetMapping("/resource-exists")
    public void throwResourceAlreadyExistsException() {
        throw ResourceAlreadyExistsException.userEmail("test@example.com");
    }
    
    @PostMapping("/validation")
    public void throwMethodArgumentNotValidException(@Valid @RequestBody UserCreateDTO user) {
        // This will trigger validation exception if the DTO is invalid
    }
    
    @GetMapping("/users/{id}")
    public void throwMethodArgumentTypeMismatchException(@PathVariable Long id) {
        // This will trigger type mismatch if non-numeric value is passed for id
    }
    
    @GetMapping("/runtime-error")
    public void throwRuntimeException() {
        throw new RuntimeException("This is an unexpected runtime error");
    }
}