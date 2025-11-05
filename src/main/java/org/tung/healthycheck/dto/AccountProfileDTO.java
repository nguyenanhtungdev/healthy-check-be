package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountProfileDTO {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String image;
    private String role;
    private LocalDate birth;
    private Double height;
    private Double weight;
    private String bloodType;
    private String fullName;
    private Boolean gender;
}