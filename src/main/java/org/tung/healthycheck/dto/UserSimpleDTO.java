package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleDTO {
    private UUID id;
    private String fullName;
    private String urlImage;
    private String phone;
    private String email;
    private LocalDate birth;
    private Boolean gender;
    private UserHealthDTO healthInfo;
    private String roleInFamily;
}
