package com.example.demo.service;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400}") // 24 hours in seconds
    private int jwtExpiration;
    
    @Value("${jwt.refresh.expiration:604800}") // 7 days in seconds
    private int jwtRefreshExpiration;
    
    /**
     * Generate access token for user
     */
    public String generateAccessToken(User user) {
        return generateToken(user, jwtExpiration);
    }
    
    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtRefreshExpiration);
    }
    
    /**
     * Generate token with custom expiration
     */
    private String generateToken(User user, int expirationSeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().toString());
        claims.put("email", user.getEmail());
        
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + (expirationSeconds * 1000));
        
        return createToken(claims, user.getEmail(), issuedAt, expiration);
    }
    
    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Get signing key for JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * Extract user ID from token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }
    
    /**
     * Extract all claims from token
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * Extract expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }
    
    /**
     * Validate token against user details
     */
    public Boolean validateToken(String token, User user) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(user.getEmail()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Validate token format and signature
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * Get remaining time until token expires (in milliseconds)
     */
    public Long getTokenExpirationTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
    
    /**
     * Generate token with additional custom claims
     */
    public String generateTokenWithCustomClaims(User user, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().toString());
        claims.put("email", user.getEmail());
        
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + (jwtExpiration * 1000));
        
        return createToken(claims, user.getEmail(), issuedAt, expiration);
    }
}
