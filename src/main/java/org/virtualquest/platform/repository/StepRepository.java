package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.virtualquest.platform.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByQuestId(Long questId);
    void deleteByQuestId(Long questId);
    @Query("SELECT s FROM Step s WHERE s.nextStep.id = ?1")
    List<Step> findByNextStepId(Long nextStepId);
}