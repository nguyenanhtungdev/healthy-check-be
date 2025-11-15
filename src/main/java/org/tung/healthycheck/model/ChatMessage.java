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
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    private String sender; // user | ai

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

