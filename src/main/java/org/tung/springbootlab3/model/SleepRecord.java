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
public class SleepRecord {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private LocalDateTime sleepDate;
    private int sleepDurationMin;
    private int deepSleepMin;
    private double sleepRate;
    private String bedTime;
    private String wakeUpTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}