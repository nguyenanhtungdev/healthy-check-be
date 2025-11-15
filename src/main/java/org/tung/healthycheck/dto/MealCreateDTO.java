package org.tung.healthycheck.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class MealCreateDTO {
    private LocalDate date;
    private String name;
    private LocalTime time;
    private List<MealItemCreateDTO> items;
    private Integer portion;
    @Data public static class MealItemCreateDTO {
        private UUID foodId;
        private String portion;
        private Integer overrideCalories;
    }
}
