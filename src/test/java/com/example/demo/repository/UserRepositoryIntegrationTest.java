package com.example.demo.repository;

import com.example.demo.base.BaseIntegrationTest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUserWhenExists() {
        // Given
        User user = createTestUser();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findByEmail(TEST_EMAIL);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.get().getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    void findByEmail_ShouldReturnEmptyWhenNotExists() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_ShouldReturnTrueWhenExists() {
        // Given
        User user = createTestUser();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmail(TEST_EMAIL);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByUsername_ShouldReturnTrueWhenExists() {
        // Given
        User user = createTestUser();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByUsername(TEST_USERNAME);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ShouldReturnFalseWhenNotExists() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistentuser");

        // Then
        assertFalse(exists);
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void findByRole_ShouldReturnUsersWithSpecificRole(Role role) {
        // Given
        User user1 = createTestUserWithRole(role);
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        
        User user2 = createTestUserWithRole(Role.USER); // Different role
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        List<User> result = userRepository.findByRole(role);

        // Then
        if (role == Role.USER) {
            assertThat(result).hasSize(2); // Both users have USER role in this case
        } else {
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRole()).isEqualTo(role);
        }
    }

    @Test
    void findActiveUsers_ShouldReturnActiveUsers() {
        // Given
        User activeUser1 = createTestUser();
        activeUser1.setEmail("active1@example.com");
        activeUser1.setUsername("active1");
        
        User activeUser2 = createTestUser();
        activeUser2.setEmail("active2@example.com");
        activeUser2.setUsername("active2");
        
        entityManager.persistAndFlush(activeUser1);
        entityManager.persistAndFlush(activeUser2);

        // When
        List<User> result = userRepository.findActiveUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
                .containsExactlyInAnyOrder("active1@example.com", "active2@example.com");
    }

    @Test
    void save_ShouldPersistUserSuccessfully() {
        // Given
        User user = createTestUser();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(savedUser.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_ShouldThrowExceptionWhenEmailDuplicate() {
        // Given
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setUsername("different_username");
        
        entityManager.persistAndFlush(user1);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            entityManager.flush();
        });
    }

    @Test
    void save_ShouldThrowExceptionWhenUsernameDuplicate() {
        // Given
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("different@example.com");
        
        entityManager.persistAndFlush(user1);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            entityManager.flush();
        });
    }

    @Test
    void delete_ShouldRemoveUserSuccessfully() {
        // Given
        User user = createTestUser();
        user = entityManager.persistAndFlush(user);
        Long userId = user.getId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        User user1 = createTestUser();
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        
        User user2 = createTestAdminUser();
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        List<User> result = userRepository.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        User user1 = createTestUser();
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        
        User user2 = createTestUser();
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void findById_ShouldReturnUserWhenExists() {
        // Given
        User user = createTestUser();
        user = entityManager.persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void save_ShouldUpdateExistingUser() {
        // Given
        User user = createTestUser();
        user = entityManager.persistAndFlush(user);
        
        // Modify user
        user.setUsername("updated_username");
        user.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(user);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getUsername()).isEqualTo("updated_username");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
    }
}