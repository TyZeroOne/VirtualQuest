package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCrudOperations() {
        // Create
        Users user = new Users();
        user.setUsername("testuser");
        user.setEmail("test" + UUID.randomUUID() + "@example.com");
        user.setPassword("password123");

        Users savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        // Read
        Users foundUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());

        // Update
        foundUser.setEmail("updated@example.com");
        userRepository.save(foundUser);

        Users updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertEquals("updated@example.com", updatedUser.getEmail());

        // Delete
        userRepository.delete(updatedUser);
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    public void testFindByUsername() {
        Users user = new Users();
        user.setUsername("uniqueuser");
        user.setEmail("unique@example.com");
        user.setPassword("123");
        userRepository.save(user);

        Users found = userRepository.findByUsernameAndDeletedFalse("uniqueuser").orElse(null);
        assertNotNull(found);
        assertEquals("unique@example.com", found.getEmail());
    }
}