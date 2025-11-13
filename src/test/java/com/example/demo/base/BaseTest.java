package com.example.demo.base;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.ProjectStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

/**
 * Base test class providing common test utilities and data
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class BaseTest {
    
    // Test Constants
    protected static final String TEST_EMAIL = "test@example.com";
    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "testpassword123";
    protected static final String ENCODED_PASSWORD = "$2a$10$abcdefghijklmnopqrstuvwxyz";
    protected static final Long TEST_USER_ID = 1L;
    protected static final Long TEST_PROJECT_ID = 1L;
    protected static final Long TEST_TASK_ID = 1L;
    protected static final String TEST_ACCESS_TOKEN = "access_token_test_123";
    protected static final String TEST_REFRESH_TOKEN = "refresh_token_test_123";
    
    // Project test constants
    protected static final String TEST_PROJECT_NAME = "Test Project";
    protected static final String TEST_PROJECT_DESCRIPTION = "Test Project Description";
    
    // Task test constants
    protected static final String TEST_TASK_TITLE = "Test Task";
    protected static final String TEST_TASK_DESCRIPTION = "Test Task Description";
    
    /**
     * Create a test user entity
     */
    protected User createTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    /**
     * Create a test admin user entity
     */
    protected User createTestAdminUser() {
        User user = createTestUser();
        user.setId(2L);
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);
        return user;
    }
    
    /**
     * Create a test project entity
     */
    protected Project createTestProject() {
        Project project = new Project();
        project.setId(TEST_PROJECT_ID);
        project.setName(TEST_PROJECT_NAME);
        project.setDescription(TEST_PROJECT_DESCRIPTION);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setOwner(createTestUser());
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
    
    /**
     * Create a test task entity
     */
    protected Task createTestTask() {
        Task task = new Task();
        task.setId(TEST_TASK_ID);
        task.setTitle(TEST_TASK_TITLE);
        task.setDescription(TEST_TASK_DESCRIPTION);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(createTestProject());
        task.setAssignee(createTestUser());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }
    
    /**
     * Create test user with specific role
     */
    protected User createTestUserWithRole(Role role) {
        User user = createTestUser();
        user.setRole(role);
        return user;
    }
    
    /**
     * Wait for async operations in tests
     */
    protected void waitForAsyncOperation() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}