package org.virtualquest.platform.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.virtualquest.platform.model.Progress;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("org.virtualquest.platform.model")
@Transactional
public class ProgressRepositoryTest {

    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private StepRepository stepRepository;

    private Users user;
    private Quest quest;
    private Step step;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        quest = new Quest();
        quest.setTitle("Test Quest");
        quest.setDifficulty(Difficulty.EASY);
        quest.setCreator(user);
        questRepository.save(quest);

        step = new Step();
        step.setQuest(quest);
        step.setStepNumber(1);
        step.setDescription("Start");
        stepRepository.save(step);
    }

    @Test
    void testFindByUserIdAndQuestId() {
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setQuest(quest);
        progress.setCurrentStep(step);
        progress.setStartedAt(LocalDateTime.now());
        progressRepository.save(progress);

        Optional<Progress> found = progressRepository.findByUserIdAndQuestId(user.getId(), quest.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void testUpdateProgress() {
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setQuest(quest);
        progress.setCurrentStep(step);
        progress.setStartedAt(LocalDateTime.now());
        progressRepository.save(progress);

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        Progress updated = progressRepository.findById(progress.getId()).orElseThrow();
        assertTrue(updated.isCompleted());
    }
}