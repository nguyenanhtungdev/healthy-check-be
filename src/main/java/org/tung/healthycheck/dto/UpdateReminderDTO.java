package org.tung.healthycheck.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateReminderDTO {
    private String title;
    private String note;
    private String category;
    private LocalDateTime remindAt;
}
