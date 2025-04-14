package org.virtualquest.platform.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Step;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("org.virtualquest.platform.model")
public class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private UserRepository userRepository;

    private Quest quest;

    @BeforeEach
    void setUp() {
        Users creator = new Users();
        creator.setUsername("creator");
        creator.setEmail("creator@example.com");
        creator.setPassword("password");
        userRepository.save(creator);

        quest = new Quest();
        quest.setTitle("Test Quest");
        quest.setDifficulty(Difficulty.EASY);
        quest.setCreator(creator);
        questRepository.save(quest);
    }

    @Test
    void testDeleteByQuestId() {
        Step step = new Step();
        step.setQuest(quest);
        step.setStepNumber(1);
        step.setDescription("First step");
        stepRepository.save(step);

        stepRepository.deleteByQuestId(quest.getId());
        assertTrue(stepRepository.findByQuestId(quest.getId()).isEmpty());
    }
}