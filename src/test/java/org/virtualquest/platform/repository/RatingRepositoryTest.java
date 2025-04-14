package org.virtualquest.platform.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Rating;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.model.enums.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@EntityScan("org.virtualquest.platform.model")
public class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestRepository questRepository;

    @BeforeEach
    void setUp() {
        Users user = new Users();
        user.setUsername("rater");
        user.setEmail("rater@example.com");
        user.setPassword("password");
        userRepository.save(user);

        Quest quest = new Quest();
        quest.setTitle("Test Quest");
        quest.setDifficulty(Difficulty.MEDIUM);
        quest.setCreator(user);
        questRepository.save(quest);
    }

    @Test
    void testFindByQuestId() {
        Users user = userRepository.findByUsername("rater").orElseThrow();
        Quest quest = questRepository.findAll().get(0);

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setQuest(quest);
        rating.setRating(4);
        ratingRepository.save(rating);

        List<Rating> found = ratingRepository.findByQuestId(quest.getId());
        assertEquals(1, found.size());
    }
}