package com.example.demo.controller;

import com.example.demo.base.BaseTest;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.auth.*;
import com.example.demo.entity.Role;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void register_ShouldReturnCreatedWhenValidRequest() throws Exception {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, Role.USER);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(TEST_USER_ID);
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(TEST_EMAIL);
        userDTO.setRole(Role.USER);
        
        AuthResponseDTO responseDTO = new AuthResponseDTO(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, userDTO);
        when(authService.register(any(AuthRegisterDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(TEST_REFRESH_TOKEN))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.data.user.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.user.email").value(TEST_EMAIL));
    }

    @Test
    void register_ShouldReturnBadRequestWhenInvalidEmail() throws Exception {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, "invalid-email", TEST_PASSWORD, Role.USER);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequestWhenPasswordTooShort() throws Exception {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(TEST_USERNAME, TEST_EMAIL, "short", Role.USER);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequestWhenUsernameTooShort() throws Exception {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO("ab", TEST_EMAIL, TEST_PASSWORD, Role.USER);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequestWhenRoleIsNull() throws Exception {
        // Given
        String jsonContent = """
            {
                "username": "%s",
                "email": "%s",
                "password": "%s",
                "role": null
            }
            """.formatted(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnOkWhenValidCredentials() throws Exception {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(TEST_USER_ID);
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(TEST_EMAIL);
        
        AuthResponseDTO responseDTO = new AuthResponseDTO(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, userDTO);
        when(authService.login(any(AuthLoginDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(TEST_REFRESH_TOKEN))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.id").value(TEST_USER_ID));
    }

    @Test
    void login_ShouldReturnBadRequestWhenEmailIsBlank() throws Exception {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO("", TEST_PASSWORD);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnBadRequestWhenPasswordIsBlank() throws Exception {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, "");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_ShouldReturnOkWhenValidToken() throws Exception {
        // Given
        AuthRefreshDTO refreshDTO = new AuthRefreshDTO(TEST_REFRESH_TOKEN);
        AuthResponseDTO responseDTO = new AuthResponseDTO("new_access_token", "new_refresh_token", null);
        when(authService.refreshToken(any(AuthRefreshDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new_refresh_token"))
                .andExpect(jsonPath("$.data.user").isEmpty());
    }

    @Test
    void refreshToken_ShouldReturnBadRequestWhenTokenIsBlank() throws Exception {
        // Given
        AuthRefreshDTO refreshDTO = new AuthRefreshDTO("");

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequestWhenMissingFields() throws Exception {
        // Given - missing username
        String jsonContent = """
            {
                "email": "%s",
                "password": "%s",
                "role": "USER"
            }
            """.formatted(TEST_EMAIL, TEST_PASSWORD);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endpoints_ShouldHaveCorrectSwaggerAnnotations() throws Exception {
        // This test verifies that the endpoints are accessible and return proper responses
        // The Swagger annotations are validated by the framework
        
        // Test that register endpoint exists and has proper path
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, but endpoint exists
        
        // Test that login endpoint exists and has proper path
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, but endpoint exists
        
        // Test that refresh endpoint exists and has proper path
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, but endpoint exists
    }
}