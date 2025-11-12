package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class TokenStoreConfig {
    
    /**
     * Database-based token storage is configured automatically via @Service annotation
     * on DatabaseTokenStoreService. Scheduling is enabled for automatic token cleanup.
     */
}