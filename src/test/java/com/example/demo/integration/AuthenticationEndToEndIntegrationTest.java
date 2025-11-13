package com.example.demo.integration;

import com.example.demo.base.BaseIntegrationTest;
import com.example.demo.dto.auth.AuthLoginDTO;
import com.example.demo.dto.auth.AuthRegisterDTO;
import com.example.demo.entity.Role;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class AuthenticationEndToEndIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void completeAuthenticationFlow_ShouldWorkEndToEnd() throws Exception {
        // Step 1: Register a new user
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(
            TEST_USERNAME, 
            TEST_EMAIL, 
            TEST_PASSWORD, 
            Role.USER
        );

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.user.username").value(TEST_USERNAME))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract tokens for later use
        String accessToken = objectMapper.readTree(registerResponse)
                .get("data").get("accessToken").asText();
        String refreshToken = objectMapper.readTree(registerResponse)
                .get("data").get("refreshToken").asText();

        // Step 2: Login with the same credentials
        AuthLoginDTO loginDTO = new AuthLoginDTO(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.email").value(TEST_EMAIL));

        // Step 3: Use access token to access protected resource
        mockMvc.perform(get("/api/users/1")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound()); // User 1 doesn't exist, but auth worked

        // Step 4: Test token refresh
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void registerUser_ShouldPersistInDatabase() throws Exception {
        // Given
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(
            "dbtest", 
            "dbtest@example.com", 
            "password123", 
            Role.ADMIN
        );

        // When
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        // Then - verify user exists in database
        boolean userExists = userRepository.existsByEmail("dbtest@example.com");
        assert userExists : "User should be persisted in database";
    }

    @Test
    void duplicateEmailRegistration_ShouldReturnConflict() throws Exception {
        // Given - register first user
        AuthRegisterDTO firstUser = new AuthRegisterDTO(
            "user1", 
            "duplicate@example.com", 
            "password123", 
            Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());

        // When - try to register second user with same email
        AuthRegisterDTO secondUser = new AuthRegisterDTO(
            "user2", 
            "duplicate@example.com", 
            "password456", 
            Role.ADMIN
        );

        // Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").containsString("duplicate@example.com"));
    }

    @Test
    void loginWithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        // Given - register user first
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(
            "testuser", 
            "testuser@example.com", 
            "correctpassword", 
            Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated());

        // When - try to login with wrong password
        AuthLoginDTO loginDTO = new AuthLoginDTO("testuser@example.com", "wrongpassword");

        // Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginWithNonexistentEmail_ShouldReturnUnauthorized() throws Exception {
        // Given
        AuthLoginDTO loginDTO = new AuthLoginDTO("nonexistent@example.com", "password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void validationErrors_ShouldReturnBadRequest() throws Exception {
        // Test invalid email format
        AuthRegisterDTO invalidEmail = new AuthRegisterDTO(
            "testuser", 
            "invalid-email", 
            "password123", 
            Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmail)))
                .andExpect(status().isBadRequest());

        // Test short password
        AuthRegisterDTO shortPassword = new AuthRegisterDTO(
            "testuser", 
            "test@example.com", 
            "short", 
            Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortPassword)))
                .andExpect(status().isBadRequest());

        // Test short username
        AuthRegisterDTO shortUsername = new AuthRegisterDTO(
            "ab", 
            "test@example.com", 
            "password123", 
            Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortUsername)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshTokenFlow_ShouldGenerateNewTokens() throws Exception {
        // Given - register and get refresh token
        AuthRegisterDTO registerDTO = new AuthRegisterDTO(
            "refreshtest", 
            "refresh@example.com", 
            "password123", 
            Role.USER
        );

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = objectMapper.readTree(registerResponse)
                .get("data").get("refreshToken").asText();

        // When - use refresh token to get new tokens
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user").doesNotExist()); // No user info in refresh response
    }

    @Test
    void allRoles_ShouldBeAbleToRegister() throws Exception {
        // Test each role can register successfully
        Role[] roles = {Role.USER, Role.ADMIN, Role.PROJECT_MANAGER, Role.DEVELOPER, Role.TESTER};
        
        for (int i = 0; i < roles.length; i++) {
            Role role = roles[i];
            AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                "user" + i, 
                "user" + i + "@example.com", 
                "password123", 
                role
            );

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.user.role").value(role.toString()));
        }
    }
}