package org.tung.healthycheck.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String title;
    private String content;
    private String type; // "lich_kham", "suc_khoe", "cap_nhat", "uu_dai", ...
    private LocalDateTime createdAt = LocalDateTime.now();
    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
