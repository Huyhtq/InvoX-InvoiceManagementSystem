package com.invox.invoice_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailResponseDTO {
    private Long id;
    // Không cần InvoiceResponseDTO ở đây để tránh vòng lặp vô hạn
    private ProductResponseDTO product; // Thông tin sản phẩm (có thể là DTO rút gọn)
    private Long quantity;
    private Long unitPrice;
    private String productNameSnapshot;
    private Long subTotal;
}