package org.tung.healthycheck.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// CalorieAggregate.java  (để lưu tổng hợp tuần/tháng định kỳ)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalorieAggregate {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String period;        // "WEEK" | "MONTH"
    private LocalDate startDate;  // ngày đầu tuần/tháng
    private LocalDate endDate;    // ngày cuối tuần/tháng
    private Integer totalCalories;
    private Integer targetCalories;
    private String status;        // "ON_TRACK", "BELOW", "ABOVE"
    private LocalDateTime computedAt = LocalDateTime.now();
}
