package org.virtualquest.platform.service;

import lombok.extern.slf4j.Slf4j;
import org.virtualquest.platform.dto.RatingDTO;
import org.virtualquest.platform.exception.DuplicateRatingException;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.model.*;
import org.virtualquest.platform.model.enums.Difficulty;
import org.virtualquest.platform.repository.RatingRepository;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class RatingService {

    private static final int EASY_MULTIPLIER = 10;
    private static final int MEDIUM_MULTIPLIER = 20;
    private static final int HARD_MULTIPLIER = 30;

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final UserService userService;

    public RatingService(RatingRepository ratingRepository,
                         UserRepository userRepository,
                         QuestRepository questRepository,
                         UserService userService) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.questRepository = questRepository;
        this.userService = userService;
    }

    // Добавить оценку квесту
    @Transactional
    public Rating addRating(Long userId, Long questId, RatingDTO dto) {
        if (ratingRepository.existsByUserIdAndQuestId(userId, questId)) {
            throw new DuplicateRatingException("User already rated this quest");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));

        // Обновить рейтинг пользователя
        int points = calculatePoints(quest.getDifficulty(), dto.getRating());
        userService.updateUserRating(userId, points);

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setQuest(quest);
        rating.setRating(dto.getRating());
        rating.setReview(dto.getReview());
        return ratingRepository.save(rating);
    }

    // Получить все оценки квеста
    public List<Rating> getRatingsByQuest(Long questId) {
        return ratingRepository.findByQuestId(questId);
    }

    // Рассчитать средний рейтинг квеста
    public double getAverageRating(Long questId) {
        return ratingRepository.findByQuestId(questId).stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    // Удалить оценку (только для автора)
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
        if (!rating.getUser().getId().equals(userId)) {
            throw new SecurityException("User is not the author of the rating");
        }
        userService.updateUserRating(userId, -calculatePoints(
                rating.getQuest().getDifficulty(),
                rating.getRating()
        ));
        ratingRepository.delete(rating);
    }

    // Расчет баллов на основе сложности
    private int calculatePoints(Difficulty difficulty, int rating) {
        return switch (difficulty) {
            case EASY -> rating * EASY_MULTIPLIER;
            case MEDIUM -> rating * MEDIUM_MULTIPLIER;
            case HARD -> rating * HARD_MULTIPLIER;
        };
    }
}