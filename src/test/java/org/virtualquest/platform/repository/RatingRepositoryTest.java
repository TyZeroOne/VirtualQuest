package org.virtualquest.platform.repository;

import jakarta.validation.ConstraintViolationException;
import org.virtualquest.platform.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingRepository ratingRepository;

    private Users testUser;
    private Quest testQuest;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setUsername("rater");
        testUser.setEmail("test" + UUID.randomUUID() + "@example.com");
        testUser.setPassword("password123");
        entityManager.persist(testUser);

        testQuest = new Quest();
        testQuest.setTitle("Dragon Slayer");
        entityManager.persist(testQuest);
    }

    @Test
    public void testCreateRating() {
        Rating rating = new Rating();
        rating.setUser(testUser);
        rating.setQuest(testQuest);
        rating.setRating(5);
        rating.setReview("Amazing experience!");

        Rating saved = ratingRepository.save(rating);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getRating());
    }

    @Test
    public void testDuplicateRatingPrevention() {
        Rating firstRating = new Rating();
        firstRating.setUser(testUser);
        firstRating.setQuest(testQuest);
        firstRating.setRating(5);

        ratingRepository.save(firstRating);
        entityManager.flush();

        Rating secondRating = new Rating();
        secondRating.setUser(testUser);
        secondRating.setQuest(testQuest);
        secondRating.setRating(4);

        assertThrows(DataIntegrityViolationException.class, () -> {
            ratingRepository.save(secondRating);
            entityManager.flush();
        });
    }

    @Test
    public void testFindByQuest() {
        Rating rating = new Rating();
        rating.setUser(testUser);
        rating.setQuest(testQuest);
        rating.setRating(5);
        ratingRepository.save(rating);
        entityManager.flush();

        List<Rating> found = ratingRepository.findByQuestId(testQuest.getId());
        assertEquals(1, found.size());
        assertEquals(5, found.get(0).getRating());
    }

    @Test
    public void testRatingConstraints() {
        Rating invalidRating = new Rating();
        invalidRating.setUser(testUser);
        invalidRating.setQuest(testQuest);
        invalidRating.setRating(6);

        assertThrows(ConstraintViolationException.class, () -> {
            ratingRepository.save(invalidRating);
            entityManager.flush();
        });
    }
}