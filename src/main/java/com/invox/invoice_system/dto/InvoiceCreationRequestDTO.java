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
    private Long employeeId; // Bắt buộc
    private String paymentMethod; // Sử dụng String để ánh xạ enum PaymentMethod ở backend
    private String notes;
    private List<InvoiceDetailRequestDTO> items; // Danh sách các mặt hàng trong hóa đơn
}
