package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.model.UserHealth;

import java.util.Optional;
import java.util.UUID;

public interface UserHealthRepository extends JpaRepository<UserHealth, UUID> {
    Optional<UserHealth> findByUser(User user);
}