package com.invox.invoice_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken; // Ví dụ: JWT Token
    private String tokenType = "Bearer";
    private UserResponseDTO user; // Thông tin người dùng cơ bản
}