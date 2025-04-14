package org.virtualquest.platform.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.virtualquest.platform.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("org.virtualquest.platform.model")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        Users user = new Users();
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        userRepository.save(user);

        Users found = userRepository.findByUsername("test_user").orElseThrow();
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    public void testExistsByUsernameOrEmail() {
        Users user = new Users();
        user.setUsername("existing_user");
        user.setEmail("existing@example.com");
        user.setPassword("password123"); // Добавлено обязательное поле
        userRepository.save(user);

        assertTrue(userRepository.existsByUsernameOrEmail("existing_user", "nonexistent@example.com"));
    }
}