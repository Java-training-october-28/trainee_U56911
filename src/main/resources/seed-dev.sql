-- Development environment seed data
INSERT INTO users (username, email, password) VALUES ('devuser1', 'dev1@example.com', 'password');
INSERT INTO users (username, email, password) VALUES ('devuser2', 'dev2@example.com', 'password');
INSERT INTO projects (name, description, owner_id) VALUES ('Dev Project 1', 'Development project 1', 1);
INSERT INTO tasks (name, description, project_id, assignee_id) VALUES ('Dev Task 1', 'Task for dev project', 1, 1);
