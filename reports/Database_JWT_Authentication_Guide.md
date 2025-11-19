# Database-Only JWT Authentication System

## Overview
This document describes the JWT authentication system using **database-only token storage** instead of Redis. All refresh tokens are stored in the PostgreSQL database with proper cleanup and management.

## Architecture Components

### 1. Core Services

#### **JwtService.java**
- JWT token generation, validation, and claims extraction
- Access tokens (24-hour expiry) and refresh tokens (7-day expiry)
- HMAC-SHA512 signing with configurable secret

#### **DatabaseTokenStoreService.java**
- Database-based refresh token storage and management
- Automatic token cleanup via scheduled tasks
- Multi-device session support with device tracking

#### **AuthService.java**
- Complete authentication service with database token integration
- User registration, login, token refresh, and logout
- Rate limiting and account lockout protection

### 2. Database Schema

#### **RefreshToken Entity**
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45)
);

-- Indexes for performance
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_active ON refresh_tokens(user_id, is_revoked, expires_at);
```

### 3. Configuration

#### **application.yaml**
```yaml
# JWT Configuration
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400  # 24 hours
  refresh:
    expiration: 604800  # 7 days

# Token storage configuration
app:
  token:
    store:
      type: database  # Database-only storage
  max:
    failed:
      attempts: 5
  account:
    lockout:
      duration: 900  # 15 minutes

# Database configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/demo_db
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## Key Features

### 1. **Database Token Storage**
- **Persistent Storage**: Tokens survive application restarts
- **Transaction Support**: ACID compliance for token operations
- **Query Optimization**: Indexed queries for fast lookups
- **Audit Trail**: Complete token creation and revocation history

### 2. **Automatic Cleanup**
- **Scheduled Task**: Runs every hour to remove expired tokens
- **Configurable Retention**: Old revoked tokens cleaned after 30 days
- **Manual Cleanup**: Administrative endpoint for immediate cleanup

### 3. **Multi-Device Support**
- **Device Tracking**: Store User-Agent and IP address
- **Device-Specific Logout**: Revoke tokens for specific devices
- **Session Management**: Track active sessions per user

### 4. **Security Features**
- **Token Rotation**: Old refresh token invalidated on use
- **Rate Limiting**: Prevent brute force attacks
- **Account Lockout**: Temporary lockout after failed attempts
- **Secure Validation**: JWT signature validation

## API Endpoints

### Authentication
```bash
# Register
POST /api/auth/register
{
  "username": "john.doe",
  "email": "john@example.com", 
  "password": "securePassword123",
  "role": "DEVELOPER"
}

# Login
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "securePassword123"
}

# Refresh Token
POST /api/auth/refresh
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}

# Logout
POST /api/auth/logout
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}

# Logout All Devices
POST /api/auth/logout-all
Authorization: Bearer <access_token>

# Validate Token
POST /api/auth/validate
Authorization: Bearer <access_token>
```

## Token Management Operations

### **DatabaseTokenStoreService Methods**

```java
// Store refresh token
void storeRefreshToken(Long userId, String refreshToken)
void storeRefreshToken(Long userId, String refreshToken, String deviceInfo, String ipAddress)

// Validate token
Long validateRefreshToken(String refreshToken)
Boolean refreshTokenExists(String refreshToken)

// Revoke tokens
void revokeRefreshToken(String refreshToken)
void revokeAllRefreshTokensForUser(Long userId)
void revokeTokensForDevice(Long userId, String deviceInfo)

// Token information
Long getRefreshTokenRemainingTime(String refreshToken)
Long getUserRefreshTokenCount(Long userId)
List<RefreshToken> getActiveRefreshTokensForUser(Long userId)

// Cleanup
void cleanupExpiredTokens()  // Scheduled every hour
int performManualCleanup()   // Manual cleanup
```

## Database Queries

### **RefreshTokenRepository Methods**

```java
// Find active tokens
Optional<RefreshToken> findActiveToken(String token, LocalDateTime now)
List<RefreshToken> findActiveTokensByUserId(Long userId, LocalDateTime now)

// Token management
@Modifying int revokeAllTokensForUser(Long userId)
@Modifying int revokeToken(String token)

// Cleanup operations
@Modifying int deleteExpiredTokens(LocalDateTime now)
@Modifying int deleteOldRevokedTokens(LocalDateTime cutoffDate)

// Statistics and monitoring
Long countActiveTokensByUserId(Long userId, LocalDateTime now)
List<RefreshToken> findTokensExpiringSoon(Long userId, LocalDateTime now, LocalDateTime threshold)
```

## Performance Considerations

### 1. **Database Optimization**
- **Indexes**: On user_id, expires_at, and composite fields
- **Query Optimization**: Use parameterized queries and proper WHERE clauses
- **Connection Pooling**: Configure HikariCP for optimal performance
- **Batch Operations**: Bulk cleanup operations

### 2. **Memory Management**
- **Lazy Loading**: Only load required token fields
- **Pagination**: For large token lists
- **Result Limiting**: Prevent memory exhaustion

### 3. **Cleanup Strategy**
- **Scheduled Cleanup**: Every hour (configurable)
- **Incremental Deletion**: Delete in batches to avoid locks
- **Index Maintenance**: Regular VACUUM and ANALYZE

## Security Best Practices

### 1. **Token Security**
- **Secure Storage**: Database with proper access controls
- **Encryption**: Consider encrypting sensitive token data
- **Access Control**: Database user with minimal permissions

### 2. **Monitoring**
- **Failed Attempts**: Log and monitor authentication failures
- **Token Usage**: Track token creation and validation patterns
- **Cleanup Metrics**: Monitor cleanup job performance

### 3. **Backup Strategy**
- **Token Backup**: Include refresh_tokens table in backups
- **Disaster Recovery**: Plan for token recreation scenarios
- **Data Retention**: Balance security with storage requirements

## Migration from Redis

### **Benefits of Database Storage**
✅ **Persistence**: Tokens survive restarts  
✅ **ACID Compliance**: Transactional consistency  
✅ **Existing Infrastructure**: Uses current database  
✅ **Audit Trail**: Complete token history  
✅ **Backup Integration**: Part of regular database backups  

### **Considerations**
⚠️ **Performance**: Slightly slower than in-memory Redis  
⚠️ **Concurrency**: Database locking vs Redis atomic operations  
⚠️ **Scaling**: May need connection pool tuning for high load  

## Monitoring and Alerts

### **Key Metrics**
- Active token count per user
- Token creation/validation rate
- Cleanup job execution time
- Database query performance
- Failed authentication attempts

### **Health Checks**
```java
// Token statistics endpoint
GET /api/admin/token-stats/{userId}

// Cleanup status
GET /api/admin/cleanup-status

// Database health
GET /actuator/health/db
```

## Testing Strategy

### **Unit Tests**
- `DatabaseTokenStoreServiceTest`: Token storage operations
- `AuthServiceTest`: Authentication flows
- `RefreshTokenRepositoryTest`: Database queries

### **Integration Tests**
- End-to-end authentication flows
- Token cleanup verification
- Multi-device session management
- Performance under load

## Production Deployment

### **Database Setup**
1. Create refresh_tokens table
2. Add appropriate indexes
3. Configure backup strategy
4. Set up monitoring

### **Application Configuration**
1. Set JWT secret via environment variable
2. Configure database connection pool
3. Enable scheduled cleanup tasks
4. Set up logging and monitoring

### **Security Checklist**
- [ ] JWT secret stored securely
- [ ] Database access restricted
- [ ] HTTPS enabled in production
- [ ] Monitoring and alerting configured
- [ ] Backup and recovery tested

## Conclusion

The database-only JWT authentication system provides a robust, scalable solution without external dependencies like Redis. It offers excellent persistence, auditability, and integrates seamlessly with existing database infrastructure while maintaining high security standards.