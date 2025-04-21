package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.virtualquest.platform.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndQuestId(Long userId, Long questId);
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.quest.id = ?1")
    long countByQuestId(Long questId);

    @Query("SELECT COUNT(p) FROM Progress p WHERE p.quest.id = ?1 AND p.completed = true")
    long countByQuestIdAndCompletedTrue(Long questId);
}