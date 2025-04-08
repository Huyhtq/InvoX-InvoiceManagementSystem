package com.invox.invoice_system.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String position;
}
