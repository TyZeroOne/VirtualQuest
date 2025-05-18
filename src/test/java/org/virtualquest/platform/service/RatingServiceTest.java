package org.virtualquest.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.RatingDTO;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.RatingRepository;
import org.virtualquest.platform.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingServiceTest {

    private RatingRepository ratingRepository;
    private UserRepository userRepository;
    private QuestRepository questRepository;
    private UserService userService;
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        userRepository = mock(UserRepository.class);
        questRepository = mock(QuestRepository.class);
        userService = mock(UserService.class);
        ratingService = new RatingService(ratingRepository, userRepository, questRepository, userService);
    }

    @Test
    void testAddRatingSuccess() {
        Long userId = 1L;
        Long questId = 2L;
        RatingDTO dto = new RatingDTO(5, "Great quest!");

        Users user = new Users();
        user.setId(userId);

        Quest quest = new Quest();
        quest.setId(questId);
        quest.setDifficulty(Difficulty.EASY);

        when(ratingRepository.existsByUserIdAndQuestId(userId, questId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(questRepository.findById(questId)).thenReturn(Optional.of(quest));
        when(ratingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Rating result = ratingService.addRating(userId, questId, dto);

        assertEquals(5, result.getRating());
        assertEquals("Great quest!", result.getReview());
        assertEquals(user, result.getUser());
        assertEquals(quest, result.getQuest());

        verify(userService).updateUserRating(userId, 50); // 5 * 10 (EASY)
    }

    @Test
    void testAddRatingDuplicate() {
        when(ratingRepository.existsByUserIdAndQuestId(1L, 1L)).thenReturn(true);
        assertThrows(DuplicateRatingException.class,
                () -> ratingService.addRating(1L, 1L, new RatingDTO(4, "Nice")));
    }

    @Test
    void testAddRatingUserNotFound() {
        when(ratingRepository.existsByUserIdAndQuestId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.addRating(1L, 1L, new RatingDTO(4, "Nice")));
    }

    @Test
    void testAddRatingQuestNotFound() {
        Users user = new Users();
        user.setId(1L);

        when(ratingRepository.existsByUserIdAndQuestId(1L, 2L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.addRating(1L, 2L, new RatingDTO(4, "Nice")));
    }

    @Test
    void testGetRatingsByQuest() {
        Rating r1 = new Rating(); r1.setRating(5);
        Rating r2 = new Rating(); r2.setRating(3);

        when(ratingRepository.findByQuestId(1L)).thenReturn(List.of(r1, r2));

        List<Rating> result = ratingService.getRatingsByQuest(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAverageRating() {
        Rating r1 = new Rating(); r1.setRating(5);
        Rating r2 = new Rating(); r2.setRating(3);

        when(ratingRepository.findByQuestId(1L)).thenReturn(List.of(r1, r2));

        double avg = ratingService.getAverageRating(1L);
        assertEquals(4.0, avg);
    }

    @Test
    void testDeleteRatingSuccess() {
        Users user = new Users(); user.setId(1L);
        Quest quest = new Quest(); quest.setDifficulty(Difficulty.HARD);
        Rating rating = new Rating();
        rating.setId(10L);
        rating.setUser(user);
        rating.setQuest(quest);
        rating.setRating(3); // 3 * 30 = 90 баллов

        when(ratingRepository.findById(10L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(10L, 1L);

        verify(userService).updateUserRating(1L, -90);
        verify(ratingRepository).delete(rating);
    }

    @Test
    void testDeleteRatingNotFound() {
        when(ratingRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> ratingService.deleteRating(10L, 1L));
    }

    @Test
    void testDeleteRatingWrongUser() {
        Users user = new Users(); user.setId(1L);
        Users another = new Users(); another.setId(2L);
        Quest quest = new Quest(); quest.setDifficulty(Difficulty.EASY);
        Rating rating = new Rating();
        rating.setId(10L);
        rating.setUser(another);
        rating.setQuest(quest);
        rating.setRating(4);

        when(ratingRepository.findById(10L)).thenReturn(Optional.of(rating));

        assertThrows(SecurityException.class,
                () -> ratingService.deleteRating(10L, 1L));
    }
}
