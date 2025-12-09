package com.example.demo.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service demonstrating various circuit breaker patterns and configurations.
 * Includes multiple circuit breaker instances with different configurations.
 */
@Service
public class CircuitBreakerDemoService {
    
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerDemoService.class);
    private final Random random = new Random();
    
    /**
     * Default circuit breaker configuration (backendA)
     * Simulates a service that fails 60% of the time
     */
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackForBackendA")
    public String demoDefaultCircuitBreaker(int requestId) {
        logger.info("Default circuit breaker - Processing request {}", requestId);
        simulateLatency(100, 300);
        
        // 60% chance of failure
        if (random.nextDouble() < 0.6) {
            throw new RuntimeException("Simulated failure in default circuit breaker for request " + requestId);
        }
        
        return "Success from default circuit breaker for request " + requestId;
    }
    
    /**
     * Slow call circuit breaker configuration (backendB)
     * Simulates slow responses that trigger circuit breaker based on slow call rate
     */
    @CircuitBreaker(name = "backendB", fallbackMethod = "fallbackForBackendB")
    public String demoSlowCallCircuitBreaker(int requestId) {
        logger.info("Slow call circuit breaker - Processing request {}", requestId);
        
        // 70% chance of slow response (> 2 seconds)
        if (random.nextDouble() < 0.7) {
            simulateLatency(2500, 4000); // Slow call
        } else {
            simulateLatency(100, 500); // Fast call
        }
        
        return "Success from slow call circuit breaker for request " + requestId;
    }
    
    /**
     * High threshold circuit breaker (backendC)
     * Requires 80% failure rate to open
     */
    @CircuitBreaker(name = "backendC", fallbackMethod = "fallbackForBackendC")
    public String demoHighThresholdCircuitBreaker(int requestId) {
        logger.info("High threshold circuit breaker - Processing request {}", requestId);
        simulateLatency(100, 400);
        
        // 50% chance of failure (below 80% threshold)
        if (random.nextDouble() < 0.5) {
            throw new RuntimeException("Simulated failure in high threshold circuit breaker for request " + requestId);
        }
        
        return "Success from high threshold circuit breaker for request " + requestId;
    }
    
    /**
     * Low threshold circuit breaker (backendD)
     * Opens at 30% failure rate
     */
    @CircuitBreaker(name = "backendD", fallbackMethod = "fallbackForBackendD")
    public String demoLowThresholdCircuitBreaker(int requestId) {
        logger.info("Low threshold circuit breaker - Processing request {}", requestId);
        simulateLatency(100, 400);
        
        // 40% chance of failure (above 30% threshold)
        if (random.nextDouble() < 0.4) {
            throw new RuntimeException("Simulated failure in low threshold circuit breaker for request " + requestId);
        }
        
        return "Success from low threshold circuit breaker for request " + requestId;
    }
    
    /**
     * Custom configuration circuit breaker (backendE)
     * Short wait duration (1s) and 60% failure threshold
     */
    @CircuitBreaker(name = "backendE", fallbackMethod = "fallbackForBackendE")
    public String demoCustomCircuitBreaker(int requestId) {
        logger.info("Custom circuit breaker - Processing request {}", requestId);
        simulateLatency(100, 400);
        
        // 70% chance of failure (above 60% threshold)
        if (random.nextDouble() < 0.7) {
            throw new RuntimeException("Simulated failure in custom circuit breaker for request " + requestId);
        }
        
        return "Success from custom circuit breaker for request " + requestId;
    }
    
    /**
     * Circuit breaker with retry mechanism
     * Combines circuit breaker with retry for transient failures
     */
    @Retry(name = "retryBackend", fallbackMethod = "fallbackWithRetry")
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackWithRetry")
    public String demoCircuitBreakerWithRetry(int requestId) {
        logger.info("Circuit breaker with retry - Processing request {}", requestId);
        simulateLatency(100, 300);
        
        // 80% chance of failure on first attempt, but retry may succeed
        if (random.nextDouble() < 0.8) {
            throw new HttpServerErrorException(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Simulated server error for request " + requestId
            );
        }
        
        return "Success after retry for request " + requestId;
    }
    
    /**
     * TimeLimiter with circuit breaker
     * Demonstrates timeout handling
     */
    @TimeLimiter(name = "timeoutBackend", fallbackMethod = "fallbackWithTimeout")
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallbackWithTimeout")
    public CompletableFuture<String> demoTimeLimiterWithCircuitBreaker(int requestId) {
        logger.info("TimeLimiter with circuit breaker - Processing request {}", requestId);
        
        return CompletableFuture.supplyAsync(() -> {
            // 50% chance of slow processing (> 3 seconds)
            if (random.nextDouble() < 0.5) {
                simulateLatency(4000, 6000); // Exceeds timeout
            } else {
                simulateLatency(100, 1000); // Within timeout
            }
            
            return "Success within timeout for request " + requestId;
        });
    }
    
    /**
     * Bulkhead pattern simulation (not directly supported by annotations in this demo)
     * Could be implemented with @Bulkhead annotation if bulkhead dependency added
     */
    public String demoBulkheadPattern(int requestId) {
        logger.info("Bulkhead pattern simulation - Processing request {}", requestId);
        simulateLatency(500, 1500);
        return "Success from bulkhead simulation for request " + requestId;
    }
    
    // Fallback methods for each circuit breaker
    
    public String fallbackForBackendA(int requestId, Throwable t) {
        logger.warn("Fallback for backendA triggered for request {}: {}", requestId, t.getMessage());
        return "Fallback response for default circuit breaker - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public String fallbackForBackendB(int requestId, Throwable t) {
        logger.warn("Fallback for backendB triggered for request {}: {}", requestId, t.getMessage());
        return "Fallback response for slow call circuit breaker - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public String fallbackForBackendC(int requestId, Throwable t) {
        logger.warn("Fallback for backendC triggered for request {}: {}", requestId, t.getMessage());
        return "Fallback response for high threshold circuit breaker - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public String fallbackForBackendD(int requestId, Throwable t) {
        logger.warn("Fallback for backendD triggered for request {}: {}", requestId, t.getMessage());
        return "Fallback response for low threshold circuit breaker - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public String fallbackForBackendE(int requestId, Throwable t) {
        logger.warn("Fallback for backendE triggered for request {}: {}", requestId, t.getMessage());
        return "Fallback response for custom circuit breaker - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public String fallbackWithRetry(int requestId, Throwable t) {
        logger.warn("Fallback with retry triggered for request {} after all retries: {}", requestId, t.getMessage());
        return "Fallback after all retries exhausted - Request " + requestId + " - Error: " + t.getMessage();
    }
    
    public CompletableFuture<String> fallbackWithTimeout(int requestId, Throwable t) {
        logger.warn("Fallback with timeout triggered for request {}: {}", requestId, t.getMessage());
        return CompletableFuture.completedFuture(
            "Fallback due to timeout - Request " + requestId + " - Error: " + t.getMessage()
        );
    }
    
    // Helper method to simulate latency
    private void simulateLatency(int minMs, int maxMs) {
        try {
            int delay = minMs + random.nextInt(maxMs - minMs + 1);
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during latency simulation", e);
        }
    }
    
    /**
     * Get circuit breaker status information
     */
    public String getCircuitBreakerInfo() {
        return """
               Available Circuit Breaker Demonstrations:
               1. Default Circuit Breaker (backendA) - 50% failure threshold, 5s wait
               2. Slow Call Circuit Breaker (backendB) - 2s slow call threshold
               3. High Threshold Circuit Breaker (backendC) - 80% failure threshold
               4. Low Threshold Circuit Breaker (backendD) - 30% failure threshold
               5. Custom Circuit Breaker (backendE) - 60% threshold, 1s wait
               6. Circuit Breaker with Retry
               7. TimeLimiter with Circuit Breaker (timeout)
               8. Bulkhead Pattern Simulation
               
               Use the corresponding endpoints to test each variation.
               """;
    }
}
