package com.example.demo.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuration class for database indexes to optimize query performance
 */
@Configuration
public class DatabaseIndexConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseIndexConfig.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates database indexes for performance optimization
     * This runs automatically when the application starts
     */
    @Bean
    @Transactional
    public CommandLineRunner createDatabaseIndexes() {
        return args -> {
            logger.info("Creating database indexes for performance optimization...");
            
            try {
                // Project table indexes
                createProjectIndexes();
                
                // Task table indexes
                createTaskIndexes();
                
                // User table indexes
                createUserIndexes();
                
                // Comment table indexes
                createCommentIndexes();
                
                logger.info("Database indexes created successfully");
            } catch (Exception e) {
                logger.warn("Failed to create some indexes (they might already exist): {}", e.getMessage());
            }
        };
    }

    private void createProjectIndexes() {
        // Index for owner_id foreign key (frequently used in queries)
        executeSql("CREATE INDEX IF NOT EXISTS idx_projects_owner_id ON projects(owner_id)");
        
        // Index for status field (if used in filtering)
        executeSql("CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status)");
        
        // Composite index for owner_id and created_at (for recent projects queries)
        executeSql("CREATE INDEX IF NOT EXISTS idx_projects_owner_created ON projects(owner_id, created_at DESC)");
        
        // Index for name search (if using LIKE queries)
        executeSql("CREATE INDEX IF NOT EXISTS idx_projects_name ON projects(name)");
        
        // Index for updated_at (for synchronization/audit purposes)
        executeSql("CREATE INDEX IF NOT EXISTS idx_projects_updated ON projects(updated_at DESC)");
        
        logger.debug("Project table indexes created");
    }

    private void createTaskIndexes() {
        // Foreign key indexes
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_assignee_id ON tasks(assignee_id)");
        
        // Status and priority indexes (common filtering fields)
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority)");
        
        // Composite indexes for common query patterns
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_project_status ON tasks(project_id, status)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_assignee_status ON tasks(assignee_id, status)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_status_priority ON tasks(status, priority DESC)");
        
        // Due date index for overdue tasks queries
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date)");
        
        // Composite index for project and due date
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_project_due_date ON tasks(project_id, due_date)");
        
        // Title search index (if using LIKE queries)
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_title ON tasks(title)");
        
        // Updated_at index for synchronization
        executeSql("CREATE INDEX IF NOT EXISTS idx_tasks_updated ON tasks(updated_at DESC)");
        
        logger.debug("Task table indexes created");
    }

    private void createUserIndexes() {
        // Email index for unique constraint and login
        executeSql("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
        
        // Username index for unique constraint
        executeSql("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
        
        // Role index for authorization queries
        executeSql("CREATE INDEX IF NOT EXISTS idx_users_role ON users(role)");
        
        logger.debug("User table indexes created");
    }

    private void createCommentIndexes() {
        // Foreign key indexes
        executeSql("CREATE INDEX IF NOT EXISTS idx_comments_task_id ON comments(task_id)");
        executeSql("CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id)");
        
        // Created_at index for chronological ordering
        executeSql("CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at DESC)");
        
        // Composite index for task and creation time
        executeSql("CREATE INDEX IF NOT EXISTS idx_comments_task_created ON comments(task_id, created_at DESC)");
        
        logger.debug("Comment table indexes created");
    }

    private void executeSql(String sql) {
        try {
            entityManager.createNativeQuery(sql).executeUpdate();
            logger.trace("Executed SQL: {}", sql);
        } catch (Exception e) {
            logger.debug("SQL execution failed (might be duplicate): {} - {}", sql, e.getMessage());
        }
    }
}
