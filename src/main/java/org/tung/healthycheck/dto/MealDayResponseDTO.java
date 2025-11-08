package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MealDayResponseDTO {
    private LocalDate date;
    private Integer totalCalories;
    private Integer targetCalories;
    private List<MealBriefDTO> meals;

    @Data @AllArgsConstructor
    public static class MealBriefDTO {
        private UUID id;
        private String name;
        private LocalTime time;
        private Integer totalCalories;
        private List<ItemBrief> items;
    }
    @Data @AllArgsConstructor
    public static class ItemBrief {
        private String name; private Integer calories; private String portion;
    }
}