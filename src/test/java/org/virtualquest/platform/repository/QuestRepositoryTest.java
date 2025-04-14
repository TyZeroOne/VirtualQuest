package org.virtualquest.platform.repository;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("org.virtualquest.platform.model")
@Transactional
public class QuestRepositoryTest {

    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Users creator = new Users();
        creator.setUsername("creator");
        creator.setEmail("creator@example.com");
        creator.setPassword("password");
        userRepository.save(creator);
    }

    @Test
    void testFindByDifficulty() {
        Quest quest = new Quest();
        quest.setTitle("Easy Quest");
        quest.setDifficulty(Difficulty.EASY);
        quest.setCreator(userRepository.findByUsername("creator").orElseThrow());
        questRepository.save(quest);

        List<Quest> found = questRepository.findByDifficulty(Difficulty.EASY);
        assertEquals(1, found.size());
    }
}