package com.invox.invoice_system.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponseDTO {
    private Long id;
    private String username;
    private Long employeeId;
    private Integer roleId;
}
