package org.virtualquest.platform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.virtualquest.platform.model.Category;
import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.Rating;
import org.virtualquest.platform.model.Users;
import org.virtualquest.platform.repository.QuestRepository;
import org.virtualquest.platform.repository.RatingRepository;
import org.virtualquest.platform.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RatingRepository ratingRepository;
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<Quest> recommendQuestsForUser(Long userId, int limit) {
        // 1. Получаем рейтинги пользователя (4 или 5)
        List<Rating> userGoodRatings = ratingRepository.findByUserIdAndRatingGreaterThanEqual(userId, 4);

        // 2. Квесты и категории, которые ему понравились
        Set<Long> likedQuestIds = userGoodRatings.stream()
                .map(r -> r.getQuest().getId())
                .collect(Collectors.toSet());

        Map<Long, Long> categoryFrequency = userGoodRatings.stream()
                .flatMap(r -> r.getQuest().getCategories().stream())
                .collect(Collectors.groupingBy(Category::getId, Collectors.counting()));

        if (likedQuestIds.isEmpty() || categoryFrequency.isEmpty()) {
            // слишком мало данных — fallback на топ игроков
            return recommendFromTopUsers(userId, limit);
        }

        Set<Long> preferredCategoryIds = categoryFrequency.keySet();

        // 3. Получаем все квесты, у которых есть хотя бы одна из категорий
        List<Quest> candidateQuests = questRepository.findAllPublishedWithCategories(preferredCategoryIds);

        // 4. Отбираем квесты, которые пользователь еще не оценивал
        candidateQuests = candidateQuests.stream()
                .filter(q -> !likedQuestIds.contains(q.getId()))
                .toList();

        if (candidateQuests.isEmpty()) {
            return recommendFromTopUsers(userId, limit);
        }

        // 5. Получаем оценки других пользователей 4 и 5
        Set<Long> candidateQuestIds = candidateQuests.stream().map(Quest::getId).collect(Collectors.toSet());
        List<Rating> goodRatingsByOthers = ratingRepository.findByQuestIdInAndRatingGreaterThanEqual(candidateQuestIds, 4);

        // 6. Группировка квестов по популярности и совпадению категорий
        Map<Quest, Long> scored = goodRatingsByOthers.stream()
                .map(Rating::getQuest)
                .filter(candidateQuests::contains)
                .collect(Collectors.groupingBy(q -> q,
                        Collectors.summingLong(q -> q.getCategories().stream()
                                .filter(c -> preferredCategoryIds.contains(c.getId()))
                                .count()
                        )));

        return scored.entrySet().stream()
                .sorted(Map.Entry.<Quest, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Quest> recommendFromTopUsers(Long currentUserId, int limit) {
        // Получаем топ-10 пользователей (кроме себя)
        List<Users> topUsers = userRepository.findAllWithCompletedQuests().stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .map(u -> Map.entry(u, userService.getUserRating(u.getId())))
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        Set<Long> ratedQuests = ratingRepository.findByUserId(currentUserId).stream()
                .map(r -> r.getQuest().getId())
                .collect(Collectors.toSet());

        // Получаем все понравившиеся квесты этих пользователей
        List<Rating> topUserRatings = topUsers.stream()
                .flatMap(u -> ratingRepository.findByUserIdAndRatingGreaterThanEqual(u.getId(), 4).stream())
                .filter(r -> !ratedQuests.contains(r.getQuest().getId()))
                .toList();

        return topUserRatings.stream()
                .map(Rating::getQuest)
                .distinct()
                .limit(limit)
                .toList();
    }
}
