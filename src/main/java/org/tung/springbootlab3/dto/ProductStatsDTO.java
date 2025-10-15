package org.tung.springbootlab3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatsDTO {
    private Long totalCount;
    private Double totalPrice;
}
