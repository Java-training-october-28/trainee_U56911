package com.example.demo.base;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.ProjectStatus;
import com.example.demo.factory.TestDataFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

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
     * Create a basic test user entity using TestDataFactory
     */
    protected User createTestUser() {
        return TestDataFactory.createUser(TEST_USERNAME);
    }

    /**
     * Create a test user with specific ID using TestDataFactory
     */
    protected User createTestUserWithId() {
        return TestDataFactory.createPersistedUser(TEST_USER_ID);
    }
    
    /**
     * Create a test admin user using TestDataFactory
     */
    protected User createTestAdminUser() {
        return TestDataFactory.createAdminUser("admin");
    }
    
    /**
     * Create a test admin user with specific ID using TestDataFactory
     */
    protected User createTestAdminUserWithId() {
        User admin = TestDataFactory.createAdminUser("admin");
        admin.setId(TEST_USER_ID);
        return admin;
    }
    
    /**
     * Create a random test user using TestDataFactory
     */
    protected User createRandomTestUser() {
        return TestDataFactory.createRandomUser();
    }
    
    /**
     * Create multiple test users using TestDataFactory
     */
    protected List<User> createTestUsers(int count) {
        return TestDataFactory.createUsers(count);
    }
    
    /**
     * Create multiple test users with specific role using TestDataFactory
     */
    protected List<User> createTestUsersWithRole(int count, Role role) {
        return TestDataFactory.createUsersWithRoles(count, role);
    }
    
    /**
     * Create a complete test user with all fields using TestDataFactory
     */
    protected User createCompleteTestUser() {
        return TestDataFactory.createValidUserWithAllFields();
    }
    
    /**
     * Create a test project entity using TestDataFactory
     */
    protected Project createTestProject() {
        User owner = createTestUser();
        return TestDataFactory.createValidProjectWithAllFields(owner);
    }
    
    /**
     * Create a test project with specific ID using TestDataFactory
     */
    protected Project createTestProjectWithId() {
        User owner = createTestUserWithId();
        Project project = TestDataFactory.createPersistedProject(TEST_PROJECT_ID, owner);
        return project;
    }
    
    /**
     * Create a test task entity using TestDataFactory
     */
    protected Task createTestTask() {
        User assignee = createTestUser();
        Project project = createTestProject();
        return TestDataFactory.createValidTaskWithAllFields(project, assignee);
    }
    
    /**
     * Create a test task with specific ID using TestDataFactory
     */
    protected Task createTestTaskWithId() {
        User assignee = createTestUserWithId();
        Project project = createTestProjectWithId();
        Task task = TestDataFactory.createPersistedTask(TEST_TASK_ID, project, assignee);
        return task;
    }
    
    /**
     * Create a project with tasks using TestDataFactory
     */
    protected Project createTestProjectWithTasks(int taskCount) {
        User owner = createTestUser();
        return TestDataFactory.createProjectWithTasks("TestProject", owner, taskCount);
    }
    
    /**
     * Create a user with complete project hierarchy using TestDataFactory
     */
    protected User createUserWithProjects(int projectCount) {
        return TestDataFactory.createUserWithCompleteProject("testuser", projectCount);
    }
    
    /**
     * Create a test user with specific role using TestDataFactory
     */
    protected User createTestUserWithRole(Role role) {
        return TestDataFactory.createUserWithRole("testuser", role);
    }
    
    /**
     * Create a complex project with team structure using TestDataFactory
     */
    protected Project createComplexTestProject(int teamSize) {
        User owner = createTestAdminUser();
        return TestDataFactory.createComplexProjectWithTeam("ComplexProject", owner, teamSize);
    }
    
    /**
     * Create team structure using TestDataFactory
     */
    protected List<User> createTestTeamStructure() {
        return TestDataFactory.createTeamStructure();
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
