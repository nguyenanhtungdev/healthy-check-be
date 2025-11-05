package org.tung.healthycheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberDTO {
    private UUID id;
    private String relation;
    private UUID ownerId;
    private UserSimpleDTO member;
}
