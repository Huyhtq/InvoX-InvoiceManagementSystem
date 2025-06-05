package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.PointTransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointTransactionResponseDTO {
    private Long id;
    private CustomerResponseDTO customer; // Thông tin khách hàng
    private Long invoiceId; // Chỉ cần ID hóa đơn, không cần DTO đầy đủ để tránh vòng lặp
    private PointTransactionType transactionType;
    private Long pointsAmount;
    private Long currentTotalPoints;
    private Long currentAvailablePoints;
    private LocalDateTime transactionDate;
    private String description;
    private String createdBy;
}