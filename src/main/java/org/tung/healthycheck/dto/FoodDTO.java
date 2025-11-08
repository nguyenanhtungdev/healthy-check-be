package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FoodDTO {
    private UUID id; private String name; private Integer calories;
    private String ingredients; private String recipe; private String imageUrl;
}