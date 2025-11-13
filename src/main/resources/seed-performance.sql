-- Performance test seed data
-- Generate 1000 users
DO $$
DECLARE i INT := 1;
BEGIN
    WHILE i <= 1000 LOOP
        INSERT INTO users (username, email, password) VALUES ('perfuser' || i, 'perf' || i || '@example.com', 'password');
        i := i + 1;
    END LOOP;
END$$;
-- Generate 100 projects
DO $$
DECLARE i INT := 1;
BEGIN
    WHILE i <= 100 LOOP
        INSERT INTO projects (name, description, owner_id) VALUES ('Perf Project ' || i, 'Performance project ' || i, i);
        i := i + 1;
    END LOOP;
END$$;
-- Generate 1000 tasks
DO $$
DECLARE i INT := 1;
BEGIN
    WHILE i <= 1000 LOOP
        INSERT INTO tasks (name, description, project_id, assignee_id) VALUES ('Perf Task ' || i, 'Task for performance project', (i % 100) + 1, (i % 1000) + 1);
        i := i + 1;
    END LOOP;
END$$;
