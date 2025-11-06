package org.tung.healthycheck.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentSchedule {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String hospitalName;
    private String frequency; // "Hàng tuần", "Hàng tháng", ...
    private LocalDate firstDate;
    private String note;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy; // chủ hộ tạo

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "appointment_participants",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();
}
