package com.invox.invoice_system.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRankDTO {
    private Long id;
    private String name;
    private Long minTotalPoints;
    private BigDecimal pointsEarningRate;
    private String description;
}