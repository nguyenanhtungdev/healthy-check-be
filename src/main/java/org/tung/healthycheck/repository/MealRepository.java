package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.Meal;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {
    List<Meal> findByUser_IdAndDate(UUID userId, LocalDate date);
}