package org.tung.healthycheck.dto;

import lombok.Data;

@Data
public class CalorieTargetDTO {
    private Integer dailyTarget;
    private Integer weeklyTarget;
    private Integer monthlyTarget;
}
