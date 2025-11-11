package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis configuration for second-level caching
 * Provides Redis-based caching for improved performance
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.timeout:2000}")
    private Duration redisTimeout;

    /**
     * Configure Redis connection
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        if (!redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(redisTimeout)
                .build();

        return new org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory(config, clientConfig);
    }

    /**
     * Configure Redis template for custom operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Configure serializers
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // String key/value serializer
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(serializer);
        
        template.setDefaultSerializer(serializer);
        template.afterPropertiesSet();
        
        return template;
    }

    /**
     * Configure cache manager with Redis
     */
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(getCacheConfiguration(Duration.ofMinutes(30)));

        // Configure different cache configurations for different cache names
        builder.withCacheConfiguration("projects", 
                getCacheConfiguration(Duration.ofMinutes(60)))
               .withCacheConfiguration("tasks", 
                getCacheConfiguration(Duration.ofMinutes(45)))
               .withCacheConfiguration("users", 
                getCacheConfiguration(Duration.ofMinutes(120)))
               .withCacheConfiguration("comments", 
                getCacheConfiguration(Duration.ofMinutes(30)))
               .withCacheConfiguration("overdue-tasks", 
                getCacheConfiguration(Duration.ofMinutes(15)));

        return builder.build();
    }

    /**
     * Create cache configuration with specific settings
     */
    private org.springframework.data.redis.cache.RedisCacheConfiguration getCacheConfiguration(Duration ttl) {
        return org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(
                        org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }
}
