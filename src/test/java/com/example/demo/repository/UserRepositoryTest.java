package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        User user = new User("testuser", "test@example.com", "password", Role.USER);
        userRepository.save(user);
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void testFindByRole() {
        User user = new User("admin", "admin@example.com", "password", Role.ADMIN);
        userRepository.save(user);
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        assertThat(admins).extracting(User::getUsername).contains("admin");
    }

    @Test
    void testPagination() {
        for (int i = 0; i < 15; i++) {
            userRepository.save(new User("user" + i, "user" + i + "@mail.com", "password", Role.USER));
        }
        var page = userRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(10);
    }
}
