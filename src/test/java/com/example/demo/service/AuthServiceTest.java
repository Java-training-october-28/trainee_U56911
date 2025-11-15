package com.example.demo.service;

import com.example.demo.base.BaseTest;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends BaseTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private DatabaseTokenStoreService databaseTokenStoreService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUserSuccessfully() {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, Role.USER);
        User newUser = createTestUser();
        User savedUser = createTestUser();
        savedUser.setId(TEST_USER_ID);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(TEST_USER_ID);
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(TEST_EMAIL);

        when(userService.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);
        when(jwtService.generateAccessToken(savedUser)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(savedUser)).thenReturn(TEST_REFRESH_TOKEN);

        // When
        AuthResponseDTO result = authService.register(registerDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(userDTO, result.getUser());
        assertTrue(result.getAccessToken().startsWith("access_token_"));
        assertTrue(result.getRefreshToken().startsWith("refresh_token_"));

        verify(userService).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userService).save(any(User.class));
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void register_ShouldThrowExceptionWhenEmailExists() {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, Role.USER);
        when(userService.existsByEmail(TEST_EMAIL)).thenReturn(true);

        // When & Then
        ResourceAlreadyExistsException exception = assertThrows(
            ResourceAlreadyExistsException.class,
            () -> authService.register(registerDTO)
        );

        assertTrue(exception.getMessage().contains(TEST_EMAIL));
        verify(userService).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).save(any(User.class));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void register_ShouldHandleAllRoles(Role role) {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, role);
        User savedUser = createTestUserWithRole(role);
        savedUser.setId(TEST_USER_ID);
        UserDTO userDTO = new UserDTO();
        userDTO.setRole(role);

        when(userService.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);

        // When
        AuthResponseDTO result = authService.register(registerDTO);

        // Then
        assertNotNull(result);
        assertEquals(role, result.getUser().getRole());
    }

    @Test
    void login_ShouldAuthenticateUserSuccessfully() {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);
        User user = createTestUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(TEST_USER_ID);
        userDTO.setEmail(TEST_EMAIL);

        when(userService.findByEmail(TEST_EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(true);
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(jwtService.generateAccessToken(user)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(TEST_REFRESH_TOKEN);

        // When
        AuthResponseDTO result = authService.login(loginDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(userDTO, result.getUser());
        assertTrue(result.getAccessToken().startsWith("access_token_"));
        assertTrue(result.getRefreshToken().startsWith("refresh_token_"));

        verify(userService).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).matches(TEST_PASSWORD, user.getPassword());
        verify(userMapper).toDTO(user);
    }

    @Test
    void login_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(loginDTO)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userService).findByEmail(TEST_EMAIL);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowExceptionWhenPasswordIncorrect() {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);
        User user = createTestUser();
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(loginDTO)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userService).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).matches(TEST_PASSWORD, user.getPassword());
        verify(userMapper, never()).toDTO(any(User.class));
    }

    @Test
    void refreshToken_ShouldGenerateNewTokens() {
        // Given
        AuthRefreshDTO refreshDTO = new AuthRefreshDTO(TEST_REFRESH_TOKEN);
        User user = createTestUser();
        UserDTO userDTO = createTestUserDTO();
        
        when(databaseTokenStoreService.validateRefreshToken(TEST_REFRESH_TOKEN))
            .thenReturn(TEST_USER_ID);
        when(userService.findById(TEST_USER_ID)).thenReturn(user);
        when(jwtService.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(TEST_NEW_REFRESH_TOKEN);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        AuthResponseDTO result = authService.refreshToken(refreshDTO);

        // Then
        assertNotNull(result);
        assertEquals(TEST_ACCESS_TOKEN, result.getAccessToken());
        assertEquals(TEST_NEW_REFRESH_TOKEN, result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertNotNull(result.getUser());
        assertEquals(userDTO.getId(), result.getUser().getId());
        
        verify(databaseTokenStoreService).revokeRefreshToken(TEST_REFRESH_TOKEN);
        verify(databaseTokenStoreService).storeRefreshToken(TEST_USER_ID, TEST_NEW_REFRESH_TOKEN);
    }

    @Test
    void register_ShouldSetUserPropertiesCorrectly() {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, Role.ADMIN);
        User savedUser = createTestUser();
        UserDTO userDTO = new UserDTO();

        when(userService.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(TEST_USERNAME, user.getUsername());
            assertEquals(TEST_EMAIL, user.getEmail());
            assertEquals(ENCODED_PASSWORD, user.getPassword());
            assertEquals(Role.ADMIN, user.getRole());
            return savedUser;
        });
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);

        // When
        authService.register(registerDTO);

        // Then
        verify(userService).save(any(User.class));
    }

    @Test
    void login_ShouldNotExposeInternalUserData() {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);
        User user = createTestUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(TEST_USER_ID);
        userDTO.setEmail(TEST_EMAIL);
        // Ensure password is not in DTO

        when(userService.findByEmail(TEST_EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(true);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        AuthResponseDTO result = authService.login(loginDTO);

        // Then
        assertNotNull(result.getUser());
        // UserDTO should not contain password - this is mapper's responsibility
        verify(userMapper).toDTO(user);
    }
}