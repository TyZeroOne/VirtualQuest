package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByDifficulty(Difficulty difficulty);
    List<Quest> findByCreatorId(Long creatorId);
    @Query("SELECT q FROM Quest q WHERE " +
            "(:published IS NULL OR q.published = :published) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "(:categoryIds IS NULL OR EXISTS (SELECT c FROM q.categories c WHERE c.id IN :categoryIds))")
    List<Quest> findByFilters(
            @Param("published") Boolean published,
            @Param("difficulty") Difficulty difficulty,
            @Param("categoryIds") List<Long> categoryIds
    );
}