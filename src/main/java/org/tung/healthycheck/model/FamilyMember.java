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
public class FamilyMember {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id") // người sở hữu danh sách gia đình
    private User owner;

    @ManyToOne
    @JoinColumn(name = "member_id") // người được thêm làm thành viên
    private User member;

    private String relation; // Ví dụ: "Vợ/Chồng", "Con", "Cha", ...
    private LocalDateTime createdAt = LocalDateTime.now();
}
