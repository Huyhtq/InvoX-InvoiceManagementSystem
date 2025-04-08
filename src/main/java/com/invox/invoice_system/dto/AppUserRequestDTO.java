package com.invox.invoice_system.dto;
// DTO : Data transfer object

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequestDTO {
    private String username;
    private String password;
    private Long employeeId;
    private Long roleId;
}
