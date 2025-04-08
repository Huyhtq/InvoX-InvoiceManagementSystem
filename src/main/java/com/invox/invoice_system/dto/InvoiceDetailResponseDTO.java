package com.invox.invoice_system.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailResponseDTO {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private Integer quantity;
    private Long unitPrice;
}
