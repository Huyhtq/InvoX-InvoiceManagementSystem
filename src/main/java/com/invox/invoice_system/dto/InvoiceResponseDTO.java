package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private CustomerResponseDTO customer; // Thông tin khách hàng
    private EmployeeResponseDTO employee; // Thông tin nhân viên
    private LocalDateTime invoiceDate;
    private Double totalAmount;
    private Double discountAmount;
    private Long pointsRedeemed;
    private Double finalAmount;
    private PaymentMethod paymentMethod;
    private InvoiceStatus status;
    private String notes;
    private LocalDateTime updatedAt;
    private List<InvoiceDetailResponseDTO> invoiceDetails; // Chi tiết hóa đơn
}