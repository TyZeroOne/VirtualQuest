package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.virtualquest.platform.model.enums.Difficulty;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ProgressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProgressRepository progressRepository;

    @Test
    public void testProgressTracking() {
        Users user = new Users();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);
        entityManager.flush();

        Quest quest = new Quest();
        quest.setTitle("Test Quest");
        quest.setDescription("Test Description");
        quest.setDifficulty(Difficulty.EASY);
        entityManager.persist(quest);
        entityManager.flush();

        Step step = new Step();
        step.setDescription("First step");
        step.setQuest(quest);
        entityManager.persist(step);
        entityManager.flush();

        Progress progress = new Progress();
        progress.setUser(user);
        progress.setQuest(quest);
        progress.setCurrentStep(step);

        Progress saved = progressRepository.save(progress);
        assertNotNull(saved.getId());
        assertNotNull(saved.getUser());
        assertNotNull(saved.getQuest());
        assertNotNull(saved.getCurrentStep());
    }
}