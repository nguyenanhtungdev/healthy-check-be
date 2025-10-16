package org.tung.springbootlab3.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleTracking {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate nextPredicted;
    private String symptoms;
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}