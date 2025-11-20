package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * Custom health indicator for cache system
 */
@Component
public class CacheHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Override
    public Health health() {
        if (cacheManager == null) {
            return Health.unknown()
                    .withDetail("cache", "not_configured")
                    .withDetail("message", "Cache manager is not available")
                    .build();
        }

        try {
            // Get cache names to verify cache manager is working
            var cacheNames = cacheManager.getCacheNames();
            
            Health.Builder status = Health.up();
            status.withDetail("cache_manager", cacheManager.getClass().getSimpleName())
                  .withDetail("cache_count", cacheNames.size())
                  .withDetail("cache_names", String.join(", ", cacheNames));
            
            // Check if specific caches are available
            String[] expectedCaches = {"users", "user", "projects", "project", "tasks", "task", "comments", "comment"};
            int availableCaches = 0;
            
            for (String cacheName : expectedCaches) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    availableCaches++;
                }
            }
            
            status.withDetail("configured_caches", expectedCaches.length)
                  .withDetail("available_caches", availableCaches);
            
            if (availableCaches < expectedCaches.length) {
                status.withDetail("warning", "Some expected caches are not available");
            }
            
            return status.build();
            
        } catch (Exception e) {
            return Health.down()
                    .withDetail("cache", "unhealthy")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
