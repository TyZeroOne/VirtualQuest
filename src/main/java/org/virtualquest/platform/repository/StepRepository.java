package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByQuestId(Long questId);
    void deleteByQuestId(Long questId);
}