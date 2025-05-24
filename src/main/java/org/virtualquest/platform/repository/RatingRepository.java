package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.virtualquest.platform.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByQuestId(Long questId);
    boolean existsByUserIdAndQuestId(Long userId, Long questId);
    List<Rating> findByUserId(Long userId);

    List<Rating> findByUserIdAndRatingGreaterThanEqual(Long userId, int minRating);

    @Query("SELECT r FROM Rating r WHERE r.quest.id IN :questIds AND r.rating >= :minRating")
    List<Rating> findByQuestIdInAndRatingGreaterThanEqual(@Param("questIds") Set<Long> questIds, @Param("minRating") int minRating);
}