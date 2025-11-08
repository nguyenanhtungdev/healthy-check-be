package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.FoodDTO;
import org.tung.healthycheck.repository.FoodRepository;

import java.util.List;

@Service
public class FoodService {
    @Autowired
    private FoodRepository foodRepository;

    public List<FoodDTO> listAll() {
        return foodRepository.findAll().stream()
                .map(f -> new FoodDTO(f.getId(), f.getName(), f.getCalories(), f.getIngredients(), f.getRecipe(), f.getImageUrl()))
                .toList();
    }

    public List<FoodDTO> search(String keyword) {
        return foodRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(f -> new FoodDTO(f.getId(), f.getName(), f.getCalories(), f.getIngredients(), f.getRecipe(), f.getImageUrl()))
                .toList();
    }
}
