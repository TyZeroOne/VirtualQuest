package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class QuestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestRepository questRepository;

    @Test
    public void testSaveAndFindQuest() {
        Users creator = new Users();
        creator.setUsername("creator");
        creator.setEmail("test@example.com");
        creator.setPassword("password123");
        entityManager.persist(creator);

        Quest quest = new Quest();
        quest.setTitle("Epic Quest");
        quest.setDifficulty(Difficulty.HARD);
        quest.setCreator(creator);

        Quest saved = questRepository.save(quest);
        assertNotNull(saved.getId());

        Quest found = questRepository.findById(saved.getId()).orElse(null);
        assertEquals("Epic Quest", found.getTitle());
        assertEquals(Difficulty.HARD, found.getDifficulty());
    }
}