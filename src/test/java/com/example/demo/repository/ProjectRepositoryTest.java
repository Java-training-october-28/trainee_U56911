package com.example.demo.repository;

import com.example.demo.entity.Project;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByOwnerIdAndPagination() {
        User owner = userRepository.save(new User("owner", "owner@mail.com", "password", Role.USER));
        for (int i = 0; i < 5; i++) {
            projectRepository.save(new Project("Project" + i, "Desc", owner));
        }
        var page = projectRepository.findByOwnerId(owner.getId(), PageRequest.of(0, 3));
        assertThat(page.getContent()).hasSize(3);
    }
}
