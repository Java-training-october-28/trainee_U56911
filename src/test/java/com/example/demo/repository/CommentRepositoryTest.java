package com.example.demo.repository;

import com.example.demo.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Make sure you have application-test.yaml for postgres config
@Transactional
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    private User user;
    private Project project;
    private Task task;

    @BeforeEach
    void setup() {
        user = userRepository.save(new User("commenter", "commenter@mail.com", "password", Role.USER));
        project = projectRepository.save(new Project("Proj", "Desc", user));
        task = new Task();
        task.setTitle("Task");
        task.setProject(project);
        task.setAssignee(user);
    task.setStatus(TaskStatus.PENDING);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task = taskRepository.save(task);
    }

    @Test
    void testFindByTaskIdAndPagination() {
        for (int i = 0; i < 6; i++) {
            Comment comment = new Comment("Comment " + i, task, user);
            commentRepository.save(comment);
        }
        var page = commentRepository.findByTaskId(task.getId(), PageRequest.of(0, 4));
        assertThat(page.getContent()).hasSize(4);
    }

    @Test
    void testFindByUserIdAndPagination() {
        for (int i = 0; i < 5; i++) {
            Comment comment = new Comment("UserComment " + i, task, user);
            commentRepository.save(comment);
        }
        var page = commentRepository.findByUserId(user.getId(), PageRequest.of(0, 3));
        assertThat(page.getContent()).hasSize(3);
    }
}
