package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Configuration for logging and request tracking
 * Provides request/response logging and correlation IDs
 */
@Configuration
public class LoggingConfig {
    
    /**
     * Request logging filter to log all HTTP requests and responses
     */
    @Bean
    public OncePerRequestFilter requestLoggingFilter() {
        return new OncePerRequestFilter() {
            
            private final Logger logger = LoggerFactory.getLogger("REQUEST_LOGGER");
            
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) throws ServletException, IOException {
                
                // Generate correlation ID for request tracking
                String correlationId = UUID.randomUUID().toString().substring(0, 8);
                
                // Log request
                long startTime = System.currentTimeMillis();
                logger.info("INCOMING REQUEST [{}] {} {} from {} at {}", 
                    correlationId,
                    request.getMethod(), 
                    request.getRequestURI(), 
                    getClientIpAddress(request),
                    LocalDateTime.now());
                
                // Log request parameters if any
                if (!request.getParameterMap().isEmpty()) {
                    logger.debug("REQUEST PARAMETERS [{}]: {}", correlationId, request.getParameterMap());
                }
                
                try {
                    // Execute the request
                    filterChain.doFilter(request, response);
                    
                    // Log successful response
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("OUTGOING RESPONSE [{}] Status: {} Duration: {}ms", 
                        correlationId, 
                        response.getStatus(), 
                        duration);
                    
                } catch (Exception e) {
                    // Log error response
                    long duration = System.currentTimeMillis() - startTime;
                    logger.error("ERROR RESPONSE [{}] Duration: {}ms Error: {}", 
                        correlationId, 
                        duration, 
                        e.getMessage(), e);
                    throw e;
                }
            }
            
            /**
             * Get client IP address from request
             */
            private String getClientIpAddress(HttpServletRequest request) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                
                return request.getRemoteAddr();
            }
        };
    }
}