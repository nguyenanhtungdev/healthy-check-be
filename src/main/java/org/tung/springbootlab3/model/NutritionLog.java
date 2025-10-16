package org.tung.springbootlab3.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionLog {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private LocalDateTime mealTime;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}