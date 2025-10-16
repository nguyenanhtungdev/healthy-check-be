package org.tung.springbootlab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.springbootlab3.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(String roleName);
}
