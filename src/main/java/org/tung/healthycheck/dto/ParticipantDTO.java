package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO {
    private UUID userId;
    private String imageUrl;
    private String fullName;
    private String email;
}
