package com.example.demo.repository;

import com.example.demo.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testFindByProjectIdAndPagination() {
        User user = userRepository.save(new User("assignee", "assignee@mail.com", "password", Role.USER));
        Project project = projectRepository.save(new Project("Proj", "Desc", user));
        for (int i = 0; i < 7; i++) {
            Task task = new Task();
            task.setTitle("Task" + i);
            task.setProject(project);
            task.setAssignee(user);
            task.setStatus(TaskStatus.PENDING);
            task.setPriority(Priority.MEDIUM);
            task.setDueDate(LocalDateTime.now().plusDays(1));
            taskRepository.save(task);
        }
        var page = taskRepository.findByProjectId(project.getId(), PageRequest.of(0, 5));
        assertThat(page.getContent()).hasSize(5);
    }

    @Test
    void testFindByStatusAndPriority() {
        User user = userRepository.save(new User("assignee2", "assignee2@mail.com", "password", Role.USER));
        Project project = projectRepository.save(new Project("Proj2", "Desc2", user));
        Task task = new Task();
        task.setTitle("TaskSpecial");
        task.setProject(project);
        task.setAssignee(user);
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDateTime.now().plusDays(2));
        taskRepository.save(task);
        var page = taskRepository.findByStatusAndPriority(TaskStatus.PENDING, Priority.MEDIUM, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }
}
