package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByQuestId(Long questId);
    boolean existsByUserIdAndQuestId(Long userId, Long questId);
}