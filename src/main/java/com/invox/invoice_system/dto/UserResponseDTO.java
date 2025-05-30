package com.invox.invoice_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private EmployeeResponseDTO employee; // Thông tin nhân viên liên kết
    private RoleDTO role;                 // Vai trò của người dùng
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}