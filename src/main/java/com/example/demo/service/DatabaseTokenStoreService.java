package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseTokenStoreService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refresh.expiration:604800}") // 7 days in seconds
    private int refreshTokenExpirationSeconds;
    
    /**
     * Store refresh token with expiry and optional device info
     */
    @Transactional
    public void storeRefreshToken(Long userId, String refreshToken, String deviceInfo, String ipAddress) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds);
        
        RefreshToken tokenEntity = new RefreshToken(refreshToken, userId, expiresAt, deviceInfo, ipAddress);
        refreshTokenRepository.save(tokenEntity);
    }
    
    /**
     * Store refresh token with expiry (without device info)
     */
    @Transactional
    public void storeRefreshToken(Long userId, String refreshToken) {
        storeRefreshToken(userId, refreshToken, null, null);
    }
    
    /**
     * Validate refresh token and get associated user ID
     */
    public Long validateRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findActiveToken(refreshToken, LocalDateTime.now());
        return tokenOpt.map(RefreshToken::getUserId).orElse(null);
    }
    
    /**
     * Check if refresh token exists and is active
     */
    public Boolean refreshTokenExists(String refreshToken) {
        return refreshTokenRepository.findActiveToken(refreshToken, LocalDateTime.now()).isPresent();
    }
    
    /**
     * Revoke a specific refresh token
     */
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        refreshTokenRepository.revokeToken(refreshToken);
    }
    
    /**
     * Revoke all refresh tokens for a user
     */
    @Transactional
    public void revokeAllRefreshTokensForUser(Long userId) {
        refreshTokenRepository.revokeAllTokensForUser(userId);
    }
    
    /**
     * Get remaining time until refresh token expires (in seconds)
     */
    public Long getRefreshTokenRemainingTime(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            if (token.isActive()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiresAt = token.getExpiresAt();
                return java.time.Duration.between(now, expiresAt).getSeconds();
            }
        }
        return 0L;
    }
    
    /**
     * Get all active refresh tokens for a user
     */
    public List<RefreshToken> getActiveRefreshTokensForUser(Long userId) {
        return refreshTokenRepository.findActiveTokensByUserId(userId, LocalDateTime.now());
    }
    
    /**
     * Get token count for a user
     */
    public Long getUserRefreshTokenCount(Long userId) {
        return refreshTokenRepository.countActiveTokensByUserId(userId, LocalDateTime.now());
    }
    
    /**
     * Extend refresh token expiry (renewal)
     */
    @Transactional
    public void extendRefreshTokenExpiry(String refreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds));
            refreshTokenRepository.save(token);
        }
    }
    
    /**
     * Get tokens expiring soon for proactive refresh
     */
    public List<RefreshToken> getTokensExpiringSoon(Long userId, int hoursAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soonThreshold = now.plusHours(hoursAhead);
        return refreshTokenRepository.findTokensExpiringSoon(userId, now, soonThreshold);
    }
    
    /**
     * Find active tokens by user and device
     */
    public List<RefreshToken> getActiveTokensByDevice(Long userId, String deviceInfo) {
        return refreshTokenRepository.findActiveTokensByUserIdAndDevice(userId, deviceInfo, LocalDateTime.now());
    }
    
    /**
     * Revoke tokens for a specific device
     */
    @Transactional
    public void revokeTokensForDevice(Long userId, String deviceInfo) {
        List<RefreshToken> deviceTokens = getActiveTokensByDevice(userId, deviceInfo);
        deviceTokens.forEach(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
    }
    
    /**
     * Get refresh token details
     */
    public Optional<RefreshToken> getRefreshTokenDetails(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }
    
    /**
     * Find tokens by IP address (for security monitoring)
     */
    public List<RefreshToken> getActiveTokensByIpAddress(String ipAddress) {
        return refreshTokenRepository.findActiveTokensByIpAddress(ipAddress, LocalDateTime.now());
    }
    
    /**
     * Scheduled cleanup of expired tokens - runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedExpired = refreshTokenRepository.deleteExpiredTokens(now);
        
        // Also cleanup old revoked tokens (older than 30 days)
        LocalDateTime cutoffDate = now.minusDays(30);
        int deletedOldRevoked = refreshTokenRepository.deleteOldRevokedTokens(cutoffDate);
        
        if (deletedExpired > 0 || deletedOldRevoked > 0) {
            System.out.println("Token cleanup completed: " + deletedExpired + " expired tokens, " + 
                             deletedOldRevoked + " old revoked tokens removed");
        }
    }
    
    /**
     * Manual cleanup method for administrative purposes
     */
    @Transactional
    public int performManualCleanup() {
        LocalDateTime now = LocalDateTime.now();
        int deletedExpired = refreshTokenRepository.deleteExpiredTokens(now);
        
        LocalDateTime cutoffDate = now.minusDays(7); // More aggressive cleanup
        int deletedOldRevoked = refreshTokenRepository.deleteOldRevokedTokens(cutoffDate);
        
        return deletedExpired + deletedOldRevoked;
    }
    
    /**
     * Get token statistics for monitoring
     */
    public TokenStatistics getTokenStatistics(Long userId) {
        List<RefreshToken> allTokens = refreshTokenRepository.findByUserId(userId);
        long activeCount = refreshTokenRepository.countActiveTokensByUserId(userId, LocalDateTime.now());
        
        return new TokenStatistics(
            allTokens.size(),
            (int) activeCount,
            allTokens.size() - (int) activeCount,
            allTokens.stream().mapToInt(t -> t.isExpired() ? 1 : 0).sum()
        );
    }
    
    /**
     * Inner class for token statistics
     */
    public static class TokenStatistics {
        private final int totalTokens;
        private final int activeTokens;
        private final int revokedTokens;
        private final int expiredTokens;
        
        public TokenStatistics(int totalTokens, int activeTokens, int revokedTokens, int expiredTokens) {
            this.totalTokens = totalTokens;
            this.activeTokens = activeTokens;
            this.revokedTokens = revokedTokens;
            this.expiredTokens = expiredTokens;
        }
        
        // Getters
        public int getTotalTokens() { return totalTokens; }
        public int getActiveTokens() { return activeTokens; }
        public int getRevokedTokens() { return revokedTokens; }
        public int getExpiredTokens() { return expiredTokens; }
        
        @Override
        public String toString() {
            return "TokenStatistics{" +
                    "total=" + totalTokens +
                    ", active=" + activeTokens +
                    ", revoked=" + revokedTokens +
                    ", expired=" + expiredTokens +
                    '}';
        }
    }
}