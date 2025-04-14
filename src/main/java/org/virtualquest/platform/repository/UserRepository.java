package org.virtualquest.platform.repository;

import org.virtualquest.platform.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);
}