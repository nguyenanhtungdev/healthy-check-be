package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    private UUID id;
    private String sender; // "user" / "ai"
    private String message;
    private LocalDateTime createdAt;
}
