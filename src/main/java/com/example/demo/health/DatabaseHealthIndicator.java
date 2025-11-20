package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Custom health indicator for database connectivity and performance
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            // Check database connectivity
            Map<String, Object> result = jdbcTemplate.queryForMap("SELECT 1 as status");
            
            // Check database performance with a simple query
            long startTime = System.currentTimeMillis();
            jdbcTemplate.queryForMap("SELECT COUNT(*) as count FROM users");
            long queryTime = System.currentTimeMillis() - startTime;
            
            Health.Builder status = Health.up();
            
            // Add performance metrics
            if (queryTime < 100) {
                status.withDetail("database", "healthy")
                      .withDetail("query_performance", "excellent")
                      .withDetail("query_time_ms", queryTime);
            } else if (queryTime < 500) {
                status.withDetail("database", "healthy")
                      .withDetail("query_performance", "good")
                      .withDetail("query_time_ms", queryTime);
            } else {
                status.withDetail("database", "healthy")
                      .withDetail("query_performance", "slow")
                      .withDetail("query_time_ms", queryTime)
                      .withDetail("warning", "Database queries are running slowly");
            }
            
            return status.build();
            
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
