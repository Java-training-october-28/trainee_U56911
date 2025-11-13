package com.example.demo.service;

import com.example.demo.base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenStoreServiceTest extends BaseTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private TokenStoreService tokenStoreService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void storeRefreshToken_ShouldStoreTokenSuccessfully() {
        // Given
        Long userId = TEST_USER_ID;
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        String expectedUserTokensKey = "user_refresh_tokens:" + userId;

        // When
        tokenStoreService.storeRefreshToken(userId, refreshToken);

        // Then
        verify(valueOperations).set(expectedTokenKey, userId.toString(), Duration.ofDays(7));
        verify(setOperations).add(expectedUserTokensKey, refreshToken);
        verify(redisTemplate).expire(expectedUserTokensKey, Duration.ofDays(7));
    }

    @Test
    void validateRefreshToken_ShouldReturnUserIdWhenTokenExists() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(valueOperations.get(expectedTokenKey)).thenReturn(TEST_USER_ID.toString());

        // When
        Long result = tokenStoreService.validateRefreshToken(refreshToken);

        // Then
        assertEquals(TEST_USER_ID, result);
        verify(valueOperations).get(expectedTokenKey);
    }

    @Test
    void validateRefreshToken_ShouldReturnNullWhenTokenDoesNotExist() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(valueOperations.get(expectedTokenKey)).thenReturn(null);

        // When
        Long result = tokenStoreService.validateRefreshToken(refreshToken);

        // Then
        assertNull(result);
        verify(valueOperations).get(expectedTokenKey);
    }

    @Test
    void revokeRefreshToken_ShouldRevokeTokenSuccessfully() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        String expectedUserTokensKey = "user_refresh_tokens:" + TEST_USER_ID;
        when(valueOperations.get(expectedTokenKey)).thenReturn(TEST_USER_ID.toString());

        // When
        tokenStoreService.revokeRefreshToken(refreshToken);

        // Then
        verify(redisTemplate).delete(expectedTokenKey);
        verify(setOperations).remove(expectedUserTokensKey, refreshToken);
    }

    @Test
    void revokeRefreshToken_ShouldHandleNonExistentToken() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(valueOperations.get(expectedTokenKey)).thenReturn(null);

        // When
        tokenStoreService.revokeRefreshToken(refreshToken);

        // Then
        verify(redisTemplate, never()).delete(anyString());
        verify(setOperations, never()).remove(anyString(), anyString());
    }

    @Test
    void revokeAllRefreshTokensForUser_ShouldRevokeAllTokens() {
        // Given
        Long userId = TEST_USER_ID;
        String expectedUserTokensKey = "user_refresh_tokens:" + userId;
        Set<String> refreshTokens = Set.of("token1", "token2", "token3");
        when(setOperations.members(expectedUserTokensKey)).thenReturn(refreshTokens);

        // When
        tokenStoreService.revokeAllRefreshTokensForUser(userId);

        // Then
        verify(redisTemplate).delete("refresh_token:token1");
        verify(redisTemplate).delete("refresh_token:token2");
        verify(redisTemplate).delete("refresh_token:token3");
        verify(redisTemplate).delete(expectedUserTokensKey);
    }

    @Test
    void getRefreshTokenRemainingTime_ShouldReturnExpireTime() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        Long expectedTime = 3600L;
        when(redisTemplate.getExpire(expectedTokenKey, TimeUnit.SECONDS)).thenReturn(expectedTime);

        // When
        Long result = tokenStoreService.getRefreshTokenRemainingTime(refreshToken);

        // Then
        assertEquals(expectedTime, result);
        verify(redisTemplate).getExpire(expectedTokenKey, TimeUnit.SECONDS);
    }

    @Test
    void refreshTokenExists_ShouldReturnTrueWhenTokenExists() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(redisTemplate.hasKey(expectedTokenKey)).thenReturn(true);

        // When
        Boolean result = tokenStoreService.refreshTokenExists(refreshToken);

        // Then
        assertTrue(result);
        verify(redisTemplate).hasKey(expectedTokenKey);
    }

    @Test
    void refreshTokenExists_ShouldReturnFalseWhenTokenDoesNotExist() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(redisTemplate.hasKey(expectedTokenKey)).thenReturn(false);

        // When
        Boolean result = tokenStoreService.refreshTokenExists(refreshToken);

        // Then
        assertFalse(result);
        verify(redisTemplate).hasKey(expectedTokenKey);
    }

    @Test
    void getActiveRefreshTokensForUser_ShouldReturnUserTokens() {
        // Given
        Long userId = TEST_USER_ID;
        String expectedUserTokensKey = "user_refresh_tokens:" + userId;
        Set<String> expectedTokens = Set.of("token1", "token2");
        when(setOperations.members(expectedUserTokensKey)).thenReturn(expectedTokens);

        // When
        Set<String> result = tokenStoreService.getActiveRefreshTokensForUser(userId);

        // Then
        assertEquals(expectedTokens, result);
        verify(setOperations).members(expectedUserTokensKey);
    }

    @Test
    void getUserRefreshTokenCount_ShouldReturnTokenCount() {
        // Given
        Long userId = TEST_USER_ID;
        String expectedUserTokensKey = "user_refresh_tokens:" + userId;
        Long expectedCount = 3L;
        when(setOperations.size(expectedUserTokensKey)).thenReturn(expectedCount);

        // When
        Long result = tokenStoreService.getUserRefreshTokenCount(userId);

        // Then
        assertEquals(expectedCount, result);
        verify(setOperations).size(expectedUserTokensKey);
    }

    @Test
    void extendRefreshTokenExpiry_ShouldExtendExpiryWhenTokenExists() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        String expectedUserTokensKey = "user_refresh_tokens:" + TEST_USER_ID;
        when(valueOperations.get(expectedTokenKey)).thenReturn(TEST_USER_ID.toString());

        // When
        tokenStoreService.extendRefreshTokenExpiry(refreshToken);

        // Then
        verify(redisTemplate).expire(expectedTokenKey, Duration.ofDays(7));
        verify(redisTemplate).expire(expectedUserTokensKey, Duration.ofDays(7));
    }

    @Test
    void extendRefreshTokenExpiry_ShouldNotExtendWhenTokenDoesNotExist() {
        // Given
        String refreshToken = TEST_REFRESH_TOKEN;
        String expectedTokenKey = "refresh_token:" + refreshToken;
        when(valueOperations.get(expectedTokenKey)).thenReturn(null);

        // When
        tokenStoreService.extendRefreshTokenExpiry(refreshToken);

        // Then
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }

    @Test
    void cleanupExpiredTokens_ShouldExecuteWithoutError() {
        // When & Then - should not throw any exception
        assertDoesNotThrow(() -> tokenStoreService.cleanupExpiredTokens());
    }
}