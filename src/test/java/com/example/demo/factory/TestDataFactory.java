package com.example.demo.factory;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.Role;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.Priority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestDataFactory {
    private static final Random random = new Random();
    
    // ==================== BASIC CREATION METHODS ====================
    
    public static User createUser(String name) {
        return new User(name, name + "@example.com", "password", Role.USER);
    }

    public static User createUser(String name, String email) {
        return new User(name, email, "password", Role.USER);
    }

    public static Project createProject(String name, User owner) {
        return new Project(name, name + " description", owner);
    }

    public static Task createTask(String name, Project project, User assignee) {
        Task task = new Task(name, name + " details", project);
        task.setAssignee(assignee);
        return task;
    }
    
    // ==================== ROLE-SPECIFIC USERS ====================
    
    public static User createAdminUser(String name) {
        return new User(name, name + "@example.com", "password", Role.ADMIN);
    }
    
    public static User createUserWithRole(String name, Role role) {
        return new User(name, name + "@example.com", "password", role);
    }
    
    public static User createValidAdmin() {
        return new User("admin", "admin@example.com", "password", Role.ADMIN);
    }
    
    public static User createValidUser() {
        return new User("user", "user@example.com", "password", Role.USER);
    }
    
    public static User createValidSuperAdmin() {
        return new User("superadmin", "superadmin@example.com", "password", Role.ADMIN);
    }
    
    // ==================== BULK CREATION METHODS ====================
    
    public static List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createUser("user" + i));
        }
        return users;
    }
    
    public static List<User> createUsersWithRoles(int count, Role role) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createUserWithRole("user" + i, role));
        }
        return users;
    }
    
    public static List<Project> createProjects(int count, User owner) {
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            projects.add(createProject("project" + i, owner));
        }
        return projects;
    }
    
    public static List<Task> createTasks(int count, Project project, User assignee) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(createTask("task" + i, project, assignee));
        }
        return tasks;
    }
    
    // ==================== COMPLETE HIERARCHIES ====================
    
    public static Project createProjectWithTasks(String name, User owner, int taskCount) {
        Project project = createProject(name, owner);
        List<Task> tasks = createTasks(taskCount, project, owner);
        project.setTasks(tasks);
        return project;
    }
    
    public static User createUserWithCompleteProject(String username, int projectsCount) {
        User user = createUser(username);
        List<Project> projects = new ArrayList<>();
        
        for (int i = 0; i < projectsCount; i++) {
            Project project = createProject("project" + i + "_" + username, user);
            List<Task> tasks = createTasks(3, project, user); // 3 tasks per project
            project.setTasks(tasks);
            projects.add(project);
        }
        
        user.setOwnedProjects(projects);
        return user;
    }
    
    public static User createUserWithProjectsAndTasks(String username, int projectsCount, int tasksPerProject) {
        User user = createUser(username);
        List<Project> projects = new ArrayList<>();
        
        for (int i = 0; i < projectsCount; i++) {
            Project project = createProject("project" + i + "_" + username, user);
            List<Task> tasks = createTasks(tasksPerProject, project, user);
            project.setTasks(tasks);
            projects.add(project);
        }
        
        user.setOwnedProjects(projects);
        return user;
    }
    
    // ==================== RANDOM DATA GENERATION ====================
    
    public static User createRandomUser() {
        String randomName = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String randomEmail = randomName + "_" + random.nextInt(1000) + "@example.com";
        Role[] roles = Role.values();
        Role randomRole = roles[random.nextInt(roles.length)];
        
        return new User(randomName, randomEmail, "password", randomRole);
    }
    
    public static Project createRandomProject(User owner) {
        String randomName = "project_" + UUID.randomUUID().toString().substring(0, 8);
        String randomDescription = "Description for " + randomName + " - " + random.nextInt(1000);
        
        return new Project(randomName, randomDescription, owner);
    }
    
    public static Task createRandomTask(Project project, User assignee) {
        String randomName = "task_" + UUID.randomUUID().toString().substring(0, 8);
        String randomDescription = "Description for " + randomName + " - " + random.nextInt(1000);
        TaskStatus[] statuses = TaskStatus.values();
        Priority[] priorities = Priority.values();
        
        Task task = new Task(randomName, randomDescription, project);
        task.setAssignee(assignee);
        task.setStatus(statuses[random.nextInt(statuses.length)]);
        task.setPriority(priorities[random.nextInt(priorities.length)]);
        task.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
        
        return task;
    }
    
    // ==================== EDGE CASES & VALIDATION ====================
    
    public static User createInvalidUser() {
        // User with null/empty required fields
        User user = new User();
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");
        return user;
    }
    
    public static Project createInvalidProject() {
        // Project with null required fields
        Project project = new Project();
        project.setName("");
        project.setDescription("");
        return project;
    }
    
    public static Task createInvalidTask() {
        // Task with null required fields
        Task task = new Task();
        task.setTitle("");
        task.setDescription("");
        return task;
    }
    
    // ==================== PERSISTENT ENTITIES (with IDs) ====================
    
    public static User createPersistedUser(Long id) {
        User user = createUser("user_" + id);
        user.setId(id);
        return user;
    }
    
    public static Project createPersistedProject(Long id, User owner) {
        Project project = createProject("project_" + id, owner);
        project.setId(id);
        return project;
    }
    
    public static Task createPersistedTask(Long id, Project project, User assignee) {
        Task task = createTask("task_" + id, project, assignee);
        task.setId(id);
        return task;
    }
    
    // ==================== SPECIALIZED SCENARIOS ====================
    
    public static User createUserForPerformanceTesting(int userIndex) {
        return new User("perf_user_" + userIndex, "perf_" + userIndex + "@example.com", "password", Role.USER);
    }
    
    public static Project createProjectForPerformanceTesting(int projectIndex, User owner) {
        return new Project("perf_project_" + projectIndex, "Performance test project " + projectIndex, owner);
    }
    
    public static Task createTaskForPerformanceTesting(int taskIndex, Project project, User assignee) {
        Task task = new Task("perf_task_" + taskIndex, "Performance test task " + taskIndex, project);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.MEDIUM);
        return task;
    }
    
    // ==================== COMPLEX SCENARIOS ====================
    
    public static List<User> createTeamStructure() {
        User admin = createAdminUser("team_admin");
        User manager = createUserWithRole("team_manager", Role.ADMIN);
        List<User> teamMembers = createUsersWithRoles(5, Role.USER);
        
        List<User> team = new ArrayList<>();
        team.add(admin);
        team.add(manager);
        team.addAll(teamMembers);
        
        return team;
    }
    
    public static Project createComplexProjectWithTeam(String projectName, User owner, int teamSize) {
        Project project = createProject(projectName, owner);
        
        // Create team members
        List<User> teamMembers = createUsersWithRoles(teamSize, Role.USER);
        List<Task> tasks = new ArrayList<>();
        Priority[] priorities = Priority.values();
        
        // Distribute tasks among team members
        for (int i = 0; i < teamSize * 2; i++) { // 2 tasks per team member
            User assignee = teamMembers.get(i % teamSize);
            Task task = createTask("complex_task_" + i, project, assignee);
            task.setPriority(priorities[i % priorities.length]);
            tasks.add(task);
        }
        
        project.setTasks(tasks);
        return project;
    }
    
    // ==================== VALIDATED ENTITIES ====================
    
    public static User createValidUserWithAllFields() {
        User user = createUser("valid_user");
        user.setEmail("valid@example.com");
        user.setPassword("validpassword123");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
    
    public static Project createValidProjectWithAllFields(User owner) {
        Project project = createProject("valid_project", owner);
        project.setDescription("This is a valid project description");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
    
    public static Task createValidTaskWithAllFields(Project project, User assignee) {
        Task task = createTask("valid_task", project, assignee);
        task.setDescription("This is a valid task description");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.HIGH);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }
}
