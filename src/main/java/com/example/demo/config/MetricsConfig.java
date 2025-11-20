package com.example.demo.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Micrometer metrics collection
 */
@Configuration
public class MetricsConfig {

    /**
     * Bean for tracking cache hit/miss metrics
     */
    @Bean
    public Timer cacheTimer(MeterRegistry registry) {
        return Timer.builder("cache.operations")
                .description("Time spent on cache operations")
                .register(registry);
    }

    /**
     * Bean for tracking database query performance
     */
    @Bean
    public Timer databaseQueryTimer(MeterRegistry registry) {
        return Timer.builder("database.queries")
                .description("Time spent on database queries")
                .register(registry);
    }

    /**
     * Bean for tracking service method execution times
     */
    @Bean
    public Timer serviceMethodTimer(MeterRegistry registry) {
        return Timer.builder("service.methods")
                .description("Time spent on service method execution")
                .register(registry);
    }
}
