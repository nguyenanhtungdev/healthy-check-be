package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.CalorieAggregate;

import java.util.List;
import java.util.UUID;

public interface CalorieAggregateRepository extends JpaRepository<CalorieAggregate, UUID> {
    List<CalorieAggregate> findByUser_IdAndPeriodOrderByStartDateDesc(UUID userId, String period);
}

