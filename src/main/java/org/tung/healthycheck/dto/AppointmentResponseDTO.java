package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private UUID id;
    private String hospitalName;
    private String frequency;
    private LocalDate firstDate;
    private String note;
    private String createdBy;
    private List<ParticipantDTO> participants;
}
