package com.invox.invoice_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreationRequestDTO {
    private Long customerId; // Có thể null nếu là khách lẻ
    private String paymentMethod; // Sử dụng String để ánh xạ enum PaymentMethod ở backend
    private String notes;
    private Integer pointsToRedeem; // Số điểm khách hàng muốn sử dụng để trừ
    private Double invoiceTaxRatePercentage;
    private List<InvoiceDetailRequestDTO> items; // Danh sách các mặt hàng trong hóa đơn
}
