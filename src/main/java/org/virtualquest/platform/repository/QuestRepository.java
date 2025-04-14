package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Quest;
import org.virtualquest.platform.model.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findByDifficulty(Difficulty difficulty);
    List<Quest> findByCreatorId(Long creatorId);
}