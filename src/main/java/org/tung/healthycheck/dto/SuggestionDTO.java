package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SuggestionDTO {
    private Integer remainingCalories;
    private List<FoodDTO> candidates;
}