package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderResponseDTO {
    private UUID id;
    private String title;
    private String note;
    private String category;
    private LocalDateTime remindAt;
    private Boolean sent;
}
