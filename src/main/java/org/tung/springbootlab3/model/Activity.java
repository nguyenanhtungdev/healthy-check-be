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
public class Activity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String activityType;
    private double durationMin;
    private double distanceKm;
    private double caloriesBurned;
    private int steps;
    private LocalDateTime recordedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}