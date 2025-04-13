package virtualquest.platform.repository;

import virtualquest.platform.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
    }
}