-- Test environment seed data
INSERT INTO users (username, email, password) VALUES ('testuser1', 'test1@example.com', 'password');
INSERT INTO users (username, email, password) VALUES ('testuser2', 'test2@example.com', 'password');
INSERT INTO projects (name, description, owner_id) VALUES ('Test Project 1', 'Test project 1', 1);
INSERT INTO tasks (name, description, project_id, assignee_id) VALUES ('Test Task 1', 'Task for test project', 1, 1);
