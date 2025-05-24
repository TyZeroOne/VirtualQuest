package org.virtualquest.platform.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.virtualquest.platform.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Query("""
        SELECT SUM(q.points) FROM Quest q
        WHERE q.published = true AND q.id IN (
            SELECT p.quest.id FROM Progress p
            WHERE p.user.id = :userId AND p.completed = true
        )
    """)
    Integer calculateUserRatingFromCompletedQuests(@Param("userId") Long userId);
    @Query("""
        SELECT u FROM Users u
        WHERE EXISTS (
            SELECT 1 FROM Progress p
            WHERE p.user = u AND p.completed = true
        )
    """)
    List<Users> findAllWithCompletedQuests();
}