package com.invox.invoice_system.dto; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; // Ví dụ: "Bad Request", "Not Found", "Internal Server Error"
    private String message; // Thông báo chi tiết lỗi
    private String path;    // Đường dẫn API gây ra lỗi
}