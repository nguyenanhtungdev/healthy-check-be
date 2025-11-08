package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.*;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.MealService;
import org.tung.healthycheck.services.TargetService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/meals")
public class MealController {
    @Autowired
    private MealService mealService;
    @Autowired private TargetService targetService;
    @Autowired private AuthService authService;

    // 1) Tạo bữa ăn trong ngày
    @PostMapping
    public ResponseEntity<Map<String,Object>> createMeal(@RequestBody MealCreateDTO dto) {
        User user = authService.getCurrentUser();
        UUID mealId = mealService.createMeal(user, dto);
        return ResponseEntity.ok(Map.of("mealId", mealId, "message", "Tạo bữa ăn thành công"));
    }

    // 2) Lấy thực đơn theo ngày
    @GetMapping
    public ResponseEntity<MealDayResponseDTO> getMealsOfDate(@RequestParam LocalDate date) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mealService.getMealsOfDate(user.getId(), date));
    }

    // 3) Đặt target calo ngày/tuần/tháng
    @PostMapping("/targets")
    public ResponseEntity<Map<String,String>> upsertTargets(@RequestBody CalorieTargetDTO dto) {
        User user = authService.getCurrentUser();
        targetService.upsertTargets(user, dto);
        return ResponseEntity.ok(Map.of("message", "Cập nhật mục tiêu calo thành công"));
    }

    // 4) Gợi ý món ăn theo calo còn thiếu của 1 ngày
    @GetMapping("/suggestions")
    public ResponseEntity<SuggestionDTO> suggestions(@RequestParam LocalDate date) {
        User user = authService.getCurrentUser();
        int remaining = mealService.getRemainingCalories(user.getId(), date);
        List<FoodDTO> candidates = mealService.suggestFoodsForRemaining(remaining);
        return ResponseEntity.ok(new SuggestionDTO(remaining, candidates));
    }
}
