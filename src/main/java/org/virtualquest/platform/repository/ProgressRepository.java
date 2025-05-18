package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.virtualquest.platform.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndQuestId(Long user, Long quest);
}