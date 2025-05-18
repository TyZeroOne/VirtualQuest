package org.virtualquest.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.virtualquest.platform.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);
}
