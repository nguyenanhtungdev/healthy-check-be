package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.Food;

import java.util.List;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, UUID> {
    List<Food> findByNameContainingIgnoreCase(String keyword);
    List<Food> findByCaloriesLessThanEqual(Integer calories);
}