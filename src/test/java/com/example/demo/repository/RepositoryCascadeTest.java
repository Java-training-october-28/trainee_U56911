package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Project;
import com.example.demo.entity.Role;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class RepositoryCascadeTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCascadeDeleteProjectRemovesTasksAndComments() {
        User owner = userRepository.save(new User("owner", "owner@mail.com", "password123", Role.USER));
        User commenter = userRepository.save(new User("commenter", "c@mail.com", "password123", Role.USER));

        Project project = projectRepository.save(new Project("Proj A", "Desc", owner));

        Task task1 = taskRepository.save(new Task("Task 1", "t1", project));
        Task task2 = taskRepository.save(new Task("Task 2", "t2", project));

        Comment c1 = commentRepository.save(new Comment("Nice", task1, commenter));
        Comment c2 = commentRepository.save(new Comment("Note", task2, commenter));

        // preconditions
        assertThat(taskRepository.findByProjectId(project.getId())).hasSize(2);
        assertThat(commentRepository.findByTaskId(task1.getId())).hasSize(1);

        // delete project - cascade should remove tasks and comments
        projectRepository.delete(project);

        // after deletion, tasks and comments should be gone
        assertThat(taskRepository.findByProjectId(project.getId())).isEmpty();
        assertThat(commentRepository.findByTaskId(task1.getId())).isEmpty();
    }

    @Test
    void testValidationConstraintsPreventInvalidProject() {
        User owner = userRepository.save(new User("owner2", "owner2@mail.com", "password123", Role.USER));

        // Name is blank -> should violate @NotBlank
        Project invalid = new Project("", "d", owner);

        assertThrows(ConstraintViolationException.class, () -> {
            projectRepository.saveAndFlush(invalid);
        });
    }
}
