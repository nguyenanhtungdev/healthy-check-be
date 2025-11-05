package org.tung.healthycheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateProfileDTO {
    private UUID userId;
    private String email;
    private String phone;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private Double height;
    private Double weight;
    private String bloodType;
    private String fullName;
    private Boolean gender;
}
