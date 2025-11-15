package com.example.demo.service;

import com.example.demo.base.BaseTest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.factory.TestDataFactory;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends BaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_ShouldSaveUserSuccessfully() {
        // Given
        User user = createTestUser();
        User savedUser = createTestUser();
        savedUser.setId(TEST_USER_ID);
        when(userRepository.save(user)).thenReturn(savedUser);

        // When
        User result = userService.save(user);

        // Then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void findById_ShouldReturnUserWhenExists() {
        // Given
        User user = createTestUser();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        // When
        User result = userService.findById(TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    void findById_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findById(TEST_USER_ID)
        );

        assertTrue(exception.getMessage().contains(TEST_USER_ID.toString()));
        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given - Enhanced factory usage with multiple roles
        List<User> users = Arrays.asList(
            createTestUser(),                    // Basic user
            createTestAdminUser(),               // Admin user
            TestDataFactory.createUserWithRole("manager", Role.ADMIN),  // Manager
            TestDataFactory.createUserWithRole("superuser", Role.ADMIN)  // Admin
        );
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findByEmail_ShouldReturnUserWhenExists() {
        // Given
        User user = createTestUser();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // When
        User result = userService.findByEmail(TEST_EMAIL);

        // Then
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void findByEmail_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findByEmail(TEST_EMAIL)
        );

        assertTrue(exception.getMessage().contains(TEST_EMAIL));
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void findByRole_ShouldReturnUsersWithSpecificRole(Role role) {
        // Given
        User user = createTestUserWithRole(role);
        List<User> users = Arrays.asList(user);
        when(userRepository.findByRole(role)).thenReturn(users);

        // When
        List<User> result = userService.findByRole(role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
        verify(userRepository).findByRole(role);
    }

    @Test
    void existsByEmail_ShouldReturnTrueWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(TEST_EMAIL);

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail(TEST_EMAIL);
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);

        // When
        boolean result = userService.existsByEmail(TEST_EMAIL);

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail(TEST_EMAIL);
    }

    @Test
    void deleteById_ShouldDeleteUserWhenExists() {
        // Given
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);

        // When
        userService.deleteById(TEST_USER_ID);

        // Then
        verify(userRepository).existsById(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
    }

    @Test
    void deleteById_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.deleteById(TEST_USER_ID)
        );

        assertTrue(exception.getMessage().contains(TEST_USER_ID.toString()));
        verify(userRepository).existsById(TEST_USER_ID);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsByUsername_ShouldReturnTrueWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // When
        boolean result = userService.existsByUsername(TEST_USERNAME);

        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername(TEST_USERNAME);
    }

    @Test
    void existsByUsername_ShouldReturnFalseWhenUsernameDoesNotExist() {
        // Given
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);

        // When
        boolean result = userService.existsByUsername(TEST_USERNAME);

        // Then
        assertFalse(result);
        verify(userRepository).existsByUsername(TEST_USERNAME);
    }

    @Test
    void findActiveUsers_ShouldReturnActiveUsers() {
        // Given - Using enhanced factory for bulk user creation
        List<User> activeUsers = TestDataFactory.createUsersWithRoles(3, Role.USER);
        activeUsers.add(TestDataFactory.createAdminUser("admin1"));
        activeUsers.add(TestDataFactory.createUserWithRole("manager", Role.ADMIN));
        when(userRepository.findActiveUsers()).thenReturn(activeUsers);

        // When
        List<User> result = userService.findActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(userRepository).findActiveUsers();
    }

    @Test
    void count_ShouldReturnUserCount() {
        // Given
        long expectedCount = 5L;
        when(userRepository.count()).thenReturn(expectedCount);

        // When
        long result = userService.count();

        // Then
        assertEquals(expectedCount, result);
        verify(userRepository).count();
    }
}
