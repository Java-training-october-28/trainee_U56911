package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Cloud API Gateway Application
 * 
 * This gateway provides:
 * 1. Dynamic routing to microservices
 * 2. Circuit breaker integration
 * 3. JWT authentication
 * 4. Rate limiting
 * 5. Request/response transformation
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
