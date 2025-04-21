package org.virtualquest.platform.service;

import org.springframework.test.context.ActiveProfiles;
import org.virtualquest.platform.dto.RatingDTO;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RatingServiceTest {
    @Mock private RatingRepository ratingRepository;
    @Mock private UserRepository userRepository;
    @Mock private QuestRepository questRepository;
    @Mock private UserService userService;
    @InjectMocks private RatingService ratingService;

    @Test
    void addRating_Success() {
        Users user = new Users();
        Quest quest = new Quest();
        quest.setDifficulty(Difficulty.HARD);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questRepository.findById(1L)).thenReturn(Optional.of(quest));
        when(ratingRepository.existsByUserIdAndQuestId(1L, 1L)).thenReturn(false);
        when(ratingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RatingDTO dto = new RatingDTO();
        dto.setRating(5);
        Rating rating = ratingService.addRating(1L, 1L, dto);

        assertEquals(5, rating.getRating());
        verify(userService).updateUserRating(1L, 150); // HARD: 5 * 30
    }

    @Test
    void deleteRating_Forbidden() {
        Rating rating = new Rating();
        Users user = new Users();
        user.setId(2L);
        rating.setUser(user); // Автор оценки — другой пользователь
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        assertThrows(SecurityException.class, () -> {
            ratingService.deleteRating(1L, 1L); // Текущий пользователь: 1L
        });
    }
}