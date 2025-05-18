package org.virtualquest.platform.repository;

import org.springframework.data.domain.Pageable;
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
    @Query("SELECT u FROM Users u ORDER BY u.rating DESC")
    List<Users> findTopNByOrderByRatingDesc(Pageable pageable);
}