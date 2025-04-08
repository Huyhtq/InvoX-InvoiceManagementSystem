package com.invox.invoice_system.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDTO {
    private String name;
    private String phone;
    private String email;
    private Integer totalPoints;
    private Integer availablePoints;
}
