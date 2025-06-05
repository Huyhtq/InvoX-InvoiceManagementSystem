package com.invox.invoice_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private Long id;
    private String username;
    private String password;
    private Long employeeId; // Liên kết với Employee đã tồn tại
    private Long roleId;     // Gán vai trò
}
