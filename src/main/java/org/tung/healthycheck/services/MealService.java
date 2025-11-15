package org.tung.healthycheck.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.FoodDTO;
import org.tung.healthycheck.dto.MealCreateDTO;
import org.tung.healthycheck.dto.MealDayResponseDTO;
import org.tung.healthycheck.model.Food;
import org.tung.healthycheck.model.Meal;
import org.tung.healthycheck.model.MealItem;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.CalorieTargetRepository;
import org.tung.healthycheck.repository.FoodRepository;
import org.tung.healthycheck.repository.MealRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;
    @Autowired private FoodRepository foodRepository;
    @Autowired private CalorieTargetRepository targetRepository;

    @Transactional
    public UUID createMeal(User user, MealCreateDTO dto) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(dto.getName());
        meal.setDate(dto.getDate());
        meal.setTime(dto.getTime());

        int total = 0;
        List<MealItem> items = new ArrayList<>();

        for (MealCreateDTO.MealItemCreateDTO it : dto.getItems()) {

            Food food = foodRepository.findById(it.getFoodId())
                    .orElseThrow(() -> new IllegalArgumentException("Food not found: " + it.getFoodId()));

            MealItem mi = new MealItem();
            mi.setMeal(meal);
            mi.setFood(food);
            mi.setName(food.getName());
            mi.setPortion(it.getPortion());

            int base = it.getOverrideCalories() != null ? it.getOverrideCalories() : food.getCalories(); // cal trên 100g
            int gram = Integer.parseInt(it.getPortion());

            int calories = base * gram / 100;
            mi.setCalories(calories);

            total += calories;
            items.add(mi);
        }

        meal.setItems(items);
        meal.setTotalCalories(total);
        mealRepository.save(meal);
        return meal.getId();
    }

    @Transactional
    public MealDayResponseDTO getMealsOfDate(UUID userId, LocalDate date) {
        List<Meal> meals = mealRepository.findByUser_IdAndDate(userId, date);
        int dayTotal = meals.stream().mapToInt(m -> Optional.ofNullable(m.getTotalCalories()).orElse(0)).sum();
        int target = targetRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .map(t -> Optional.ofNullable(t.getDailyTarget()).orElse(0)).orElse(0);

        List<MealDayResponseDTO.MealBriefDTO> mealDtos = meals.stream().map(m ->
                new MealDayResponseDTO.MealBriefDTO(
                        m.getId(), m.getName(), m.getTime(), m.getTotalCalories(),
                        m.getItems().stream().map(it -> new MealDayResponseDTO.ItemBrief(it.getName(), it.getCalories(), it.getPortion())).toList()
                )
        ).toList();

        return new MealDayResponseDTO(date, dayTotal, target, mealDtos);
    }
    @Transactional
    public int getRemainingCalories(UUID userId, LocalDate date) {
        MealDayResponseDTO day = getMealsOfDate(userId, date);
        return Math.max(0, Optional.ofNullable(day.getTargetCalories()).orElse(0) - Optional.ofNullable(day.getTotalCalories()).orElse(0));
    }

    public List<FoodDTO> suggestFoodsForRemaining(Integer remaining) {
        if (remaining <= 0) {
            return List.of();  // Không gợi ý
        }
        // cửa sổ +-10% để dễ gợi ý (vd còn thiếu 400 => gợi ý <= 440)
        int threshold = (int)Math.round(remaining * 1.10);
        return foodRepository.findByCaloriesLessThanEqual(threshold).stream()
                .sorted(Comparator.comparingInt(Food::getCalories).reversed()) // món gần ngưỡng trước
                .map(f -> new FoodDTO(f.getId(), f.getName(), f.getCalories(), f.getIngredients(), f.getRecipe(), f.getImageUrl()))
                .toList();
    }
}
