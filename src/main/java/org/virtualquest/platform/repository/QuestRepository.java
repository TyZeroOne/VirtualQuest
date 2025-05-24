    package org.virtualquest.platform.repository;

    import org.springframework.data.jpa.repository.EntityGraph;
    import org.springframework.data.jpa.repository.Modifying;
    import org.virtualquest.platform.model.Quest;
    import org.virtualquest.platform.model.enums.Difficulty;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import java.util.List;
    import java.util.Optional;
    import java.util.Set;

    public interface QuestRepository extends JpaRepository<Quest, Long> {
        List<Quest> findByDifficulty(Difficulty difficulty);
        List<Quest> findByCreatorId(Long creatorId);
        @Query("""
            SELECT DISTINCT q
            FROM Quest q
            JOIN q.categories c
            WHERE (:published IS NULL OR q.published = :published)
              AND (:difficulty IS NULL OR q.difficulty = :difficulty)
              AND (:categoryIds IS NULL OR c.id IN :categoryIds)
        """)
        List<Quest> findByFilters(
                @Param("published") boolean published,
                @Param("difficulty") Difficulty difficulty,
                @Param("categoryIds") List<Long> categoryIds
        );
        @Modifying
        @Query("UPDATE Quest q SET q.startedCount = q.startedCount + 1 WHERE q.id = :questId")
        int incrementStartedCount(@Param("questId") Long questId);
        @EntityGraph(attributePaths = "steps")
        @Query("SELECT DISTINCT q FROM Quest q LEFT JOIN FETCH q.steps WHERE q.id = :questId")
        Optional<Quest> findByIdWithSteps(@Param("questId") Long questId);
        @Query("""
            SELECT DISTINCT q FROM Quest q
            JOIN q.categories c
            WHERE q.published = true AND c.id IN :categoryIds
        """)
        List<Quest> findAllPublishedWithCategories(@Param("categoryIds") Set<Long> categoryIds);
        @Query("""
            SELECT q FROM Quest q
            WHERE q.startedCount >= :startedCount AND q.autoCalculated = :autoCalculated
        """)
        List<Quest> findByStartedCountGreaterThanEqual(
                @Param("startedCount") int startedCount,
                @Param("autoCalculated") boolean autoCalculated);
    }