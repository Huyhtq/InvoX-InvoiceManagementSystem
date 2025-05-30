package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.enums.PointTransactionType;

import java.util.List;

public interface PointTransactionService {
    List<PointTransactionResponseDTO> getPointTransactionsByCustomerId(Long customerId);
    // Phương thức nội bộ, không phải public API
    void createPointTransaction(Customer customer, Invoice invoice, PointTransactionType type, Long pointsAmount, String description);
}