package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class TokenStoreService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_REFRESH_TOKENS_PREFIX = "user_refresh_tokens:";
    private static final String REFRESH_TOKEN_EXPIRY_DAYS = 7;
    
    /**
     * Store refresh token for user with expiry
     */
    public void storeRefreshToken(Long userId, String refreshToken) {
        // Store refresh token with user ID as value
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), 
            Duration.ofDays(REFRESH_TOKEN_EXPIRY_DAYS));
        
        // Also store reverse mapping: user -> their refresh tokens
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        redisTemplate.opsForSet().add(userTokensKey, refreshToken);
        redisTemplate.expire(userTokensKey, Duration.ofDays(REFRESH_TOKEN_EXPIRY_DAYS));
    }
    
    /**
     * Validate if refresh token exists and get associated user ID
     */
    public Long validateRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        
        if (userId != null) {
            return Long.parseLong(userId);
        }
        return null;
    }
    
    /**
     * Revoke a specific refresh token
     */
    public void revokeRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        
        if (userId != null) {
            // Remove from token->user mapping
            redisTemplate.delete(tokenKey);
            
            // Remove from user->tokens mapping
            String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
            redisTemplate.opsForSet().remove(userTokensKey, refreshToken);
        }
    }
    
    /**
     * Revoke all refresh tokens for a user
     */
    public void revokeAllRefreshTokensForUser(Long userId) {
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        
        // Get all refresh tokens for this user
        var refreshTokens = redisTemplate.opsForSet().members(userTokensKey);
        
        if (refreshTokens != null) {
            // Revoke each token
            for (String refreshToken : refreshTokens) {
                String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
                redisTemplate.delete(tokenKey);
            }
        }
        
        // Remove the user tokens set
        redisTemplate.delete(userTokensKey);
    }
    
    /**
     * Get remaining time until refresh token expires (in seconds)
     */
    public Long getRefreshTokenRemainingTime(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
    }
    
    /**
     * Check if refresh token exists
     */
    public Boolean refreshTokenExists(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }
    
    /**
     * Get all active refresh tokens for a user
     */
    public java.util.Set<String> getActiveRefreshTokensForUser(Long userId) {
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        return redisTemplate.opsForSet().members(userTokensKey);
    }
    
    /**
     * Clean up expired tokens (this would typically be called by a scheduled job)
     */
    public void cleanupExpiredTokens() {
        // Redis handles TTL automatically, so this is mainly for demonstration
        // In a real scenario, you might want to log or monitor token cleanup
        System.out.println("Token cleanup method called - Redis handles TTL automatically");
    }
    
    /**
     * Get token count for a user
     */
    public Long getUserRefreshTokenCount(Long userId) {
        String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
        return redisTemplate.opsForSet().size(userTokensKey);
    }
    
    /**
     * Extend refresh token expiry (renewal)
     */
    public void extendRefreshTokenExpiry(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        
        if (userId != null) {
            // Update expiry for token->user mapping
            redisTemplate.expire(tokenKey, Duration.ofDays(REFRESH_TOKEN_EXPIRY_DAYS));
            
            // Update expiry for user->tokens mapping
            String userTokensKey = USER_REFRESH_TOKENS_PREFIX + userId;
            redisTemplate.expire(userTokensKey, Duration.ofDays(REFRESH_TOKEN_EXPIRY_DAYS));
        }
    }
}
