package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.CalorieTarget;

import java.util.Optional;
import java.util.UUID;

public interface CalorieTargetRepository extends JpaRepository<CalorieTarget, UUID> {
    Optional<CalorieTarget> findTopByUser_IdOrderByCreatedAtDesc(UUID userId);
}
