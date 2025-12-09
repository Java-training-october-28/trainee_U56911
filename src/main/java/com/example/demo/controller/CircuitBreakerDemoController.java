package com.example.demo.controller;

import com.example.demo.service.CircuitBreakerDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller for demonstrating various circuit breaker patterns and configurations.
 * Provides endpoints to test different circuit breaker variations.
 */
@RestController
@RequestMapping("/api/circuit-breaker")
@Tag(name = "Circuit Breaker Demo", description = "Endpoints for demonstrating circuit breaker patterns with various configurations")
public class CircuitBreakerDemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerDemoController.class);
    private final CircuitBreakerDemoService circuitBreakerDemoService;
    private final AtomicInteger requestCounter = new AtomicInteger(1);
    
    public CircuitBreakerDemoController(CircuitBreakerDemoService circuitBreakerDemoService) {
        this.circuitBreakerDemoService = circuitBreakerDemoService;
    }
    
    @GetMapping("/info")
    @Operation(summary = "Get circuit breaker demo information", 
               description = "Returns information about available circuit breaker demonstrations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved circuit breaker info")
    })
    public ResponseEntity<String> getCircuitBreakerInfo() {
        return ResponseEntity.ok(circuitBreakerDemoService.getCircuitBreakerInfo());
    }
    
    @GetMapping("/default")
    @Operation(summary = "Test default circuit breaker", 
               description = "Tests the default circuit breaker configuration (backendA) with 50% failure threshold")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testDefaultCircuitBreaker() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing default circuit breaker with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoDefaultCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/slow-call")
    @Operation(summary = "Test slow call circuit breaker", 
               description = "Tests circuit breaker configured for slow calls (backendB) with 2s threshold")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testSlowCallCircuitBreaker() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing slow call circuit breaker with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoSlowCallCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/high-threshold")
    @Operation(summary = "Test high threshold circuit breaker", 
               description = "Tests circuit breaker with high failure threshold (backendC) - 80% failure rate required to open")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testHighThresholdCircuitBreaker() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing high threshold circuit breaker with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoHighThresholdCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/low-threshold")
    @Operation(summary = "Test low threshold circuit breaker", 
               description = "Tests circuit breaker with low failure threshold (backendD) - opens at 30% failure rate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testLowThresholdCircuitBreaker() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing low threshold circuit breaker with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoLowThresholdCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/custom")
    @Operation(summary = "Test custom circuit breaker", 
               description = "Tests custom circuit breaker configuration (backendE) with 60% threshold and 1s wait duration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testCustomCircuitBreaker() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing custom circuit breaker with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoCustomCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/with-retry")
    @Operation(summary = "Test circuit breaker with retry", 
               description = "Tests circuit breaker combined with retry mechanism for transient failures")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<String> testCircuitBreakerWithRetry() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing circuit breaker with retry with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoCircuitBreakerWithRetry(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/with-timeout")
    @Operation(summary = "Test circuit breaker with timeout", 
               description = "Tests circuit breaker combined with time limiter (3s timeout)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Circuit breaker test completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error (simulated)")
    })
    public ResponseEntity<CompletableFuture<String>> testCircuitBreakerWithTimeout() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing circuit breaker with timeout with request ID: {}", requestId);
        CompletableFuture<String> result = circuitBreakerDemoService.demoTimeLimiterWithCircuitBreaker(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/bulkhead")
    @Operation(summary = "Test bulkhead pattern simulation", 
               description = "Tests bulkhead pattern simulation (concurrent request limiting)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulkhead test completed")
    })
    public ResponseEntity<String> testBulkheadPattern() {
        int requestId = requestCounter.getAndIncrement();
        logger.info("Testing bulkhead pattern with request ID: {}", requestId);
        String result = circuitBreakerDemoService.demoBulkheadPattern(requestId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/batch-test")
    @Operation(summary = "Batch test all circuit breakers", 
               description = "Runs a batch test of all circuit breaker variations and returns aggregated results")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch test completed")
    })
    public ResponseEntity<String> batchTestAllCircuitBreakers(
            @Parameter(description = "Number of requests per circuit breaker", example = "5")
            @RequestParam(defaultValue = "5") int requestsPerBreaker) {
        
        logger.info("Starting batch test with {} requests per circuit breaker", requestsPerBreaker);
        StringBuilder results = new StringBuilder();
        results.append("=== Circuit Breaker Batch Test Results ===\n\n");
        
        // Test each circuit breaker multiple times
        String[] testNames = {
            "Default", "Slow Call", "High Threshold", "Low Threshold", 
            "Custom", "With Retry", "Bulkhead"
        };
        
        for (String testName : testNames) {
            results.append(testName).append(" Circuit Breaker:\n");
            int successCount = 0;
            int failureCount = 0;
            
            for (int i = 0; i < requestsPerBreaker; i++) {
                try {
                    int requestId = requestCounter.getAndIncrement();
                    String result = "";
                    
                    switch (testName) {
                        case "Default":
                            result = circuitBreakerDemoService.demoDefaultCircuitBreaker(requestId);
                            break;
                        case "Slow Call":
                            result = circuitBreakerDemoService.demoSlowCallCircuitBreaker(requestId);
                            break;
                        case "High Threshold":
                            result = circuitBreakerDemoService.demoHighThresholdCircuitBreaker(requestId);
                            break;
                        case "Low Threshold":
                            result = circuitBreakerDemoService.demoLowThresholdCircuitBreaker(requestId);
                            break;
                        case "Custom":
                            result = circuitBreakerDemoService.demoCustomCircuitBreaker(requestId);
                            break;
                        case "With Retry":
                            result = circuitBreakerDemoService.demoCircuitBreakerWithRetry(requestId);
                            break;
                        case "Bulkhead":
                            result = circuitBreakerDemoService.demoBulkheadPattern(requestId);
                            break;
                    }
                    
                    if (result.contains("Success") || result.contains("Fallback")) {
                        successCount++;
                    }
                } catch (Exception e) {
                    failureCount++;
                }
            }
            
            results.append("  Success/Fallback: ").append(successCount)
                   .append(", Failures: ").append(failureCount)
                   .append(", Total: ").append(requestsPerBreaker)
                   .append("\n\n");
        }
        
        results.append("=== End of Batch Test ===\n");
        results.append("Note: 'Success/Fallback' includes both successful responses and fallback responses.\n");
        results.append("Fallback responses indicate circuit breaker is working correctly.\n");
        
        return ResponseEntity.ok(results.toString());
    }
    
    @GetMapping("/health")
    @Operation(summary = "Circuit breaker health status", 
               description = "Returns health status of circuit breaker configurations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Health status retrieved")
    })
    public ResponseEntity<String> getCircuitBreakerHealth() {
        return ResponseEntity.ok("""
            Circuit Breaker Health Status:
            - All circuit breaker configurations are active
            - Health indicators are registered with Spring Boot Actuator
            - Monitor at: /actuator/health/circuitbreakers
            - Circuit breaker metrics at: /actuator/metrics/resilience4j.circuitbreaker
            """);
    }
    
    @GetMapping("/reset-counter")
    @Operation(summary = "Reset request counter", 
               description = "Resets the internal request counter for demo purposes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Counter reset successfully")
    })
    public ResponseEntity<String> resetRequestCounter() {
        int oldValue = requestCounter.get();
        requestCounter.set(1);
        return ResponseEntity.ok("Request counter reset from " + oldValue + " to 1");
    }
}
