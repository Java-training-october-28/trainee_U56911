package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Find active refresh token by token string
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.isRevoked = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findActiveToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    /**
     * Find refresh token by token string (regardless of status)
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Find all active refresh tokens for a user
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * Find all refresh tokens for a user (active and inactive)
     */
    List<RefreshToken> findByUserId(Long userId);
    
    /**
     * Count active refresh tokens for a user
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isRevoked = false AND rt.expiresAt > :now")
    Long countActiveTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * Revoke all refresh tokens for a user
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.userId = :userId AND rt.isRevoked = false")
    int revokeAllTokensForUser(@Param("userId") Long userId);
    
    /**
     * Revoke a specific refresh token
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
    int revokeToken(@Param("token") String token);
    
    /**
     * Delete expired tokens (cleanup job)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Delete revoked tokens older than specified date (cleanup job)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.isRevoked = true AND rt.createdAt < :cutoffDate")
    int deleteOldRevokedTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find tokens expiring soon (for proactive refresh)
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.isRevoked = false AND rt.expiresAt BETWEEN :now AND :soonThreshold")
    List<RefreshToken> findTokensExpiringSoon(@Param("userId") Long userId, 
                                             @Param("now") LocalDateTime now, 
                                             @Param("soonThreshold") LocalDateTime soonThreshold);
    
    /**
     * Find tokens by device info (for device-specific management)
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.deviceInfo = :deviceInfo AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUserIdAndDevice(@Param("userId") Long userId, 
                                                        @Param("deviceInfo") String deviceInfo, 
                                                        @Param("now") LocalDateTime now);
    
    /**
     * Find tokens by IP address (for security monitoring)
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.ipAddress = :ipAddress AND rt.isRevoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveTokensByIpAddress(@Param("ipAddress") String ipAddress, @Param("now") LocalDateTime now);
}