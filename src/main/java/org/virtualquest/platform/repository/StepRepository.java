package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.virtualquest.platform.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByQuestId(Long questId);
    void deleteByQuestId(Long questId);
    List<Step> findByNextStepId(Long nextStepId);
    @Query("SELECT COUNT(s) FROM Step s WHERE s.quest.id = :questId")
    Long countByQuestId(@Param("questId") Long questId);
}