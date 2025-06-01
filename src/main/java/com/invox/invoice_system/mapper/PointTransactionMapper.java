package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.PointTransaction;
import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class PointTransactionMapper {

    private final CustomerMapper customerMapper; // Inject CustomerMapper

    // Convert PointTransaction Entity to PointTransactionResponseDTO
    public PointTransactionResponseDTO toResponseDto(PointTransaction pointTransaction) {
        if (pointTransaction == null) {
            return null;
        }

        PointTransactionResponseDTO dto = new PointTransactionResponseDTO();
        dto.setId(pointTransaction.getId());
        dto.setPointsAmount(pointTransaction.getPointsAmount());
        dto.setCurrentTotalPoints(pointTransaction.getCurrentTotalPoints());
        dto.setCurrentAvailablePoints(pointTransaction.getCurrentAvailablePoints()); 
        dto.setTransactionDate(pointTransaction.getTransactionDate());
        dto.setDescription(pointTransaction.getDescription());
        dto.setCreatedBy(pointTransaction.getCreatedBy());
        dto.setTransactionType(pointTransaction.getTransactionType()); // Enum PointTransactionType

        // Ánh xạ Customer Entity sang CustomerResponseDTO
        if (pointTransaction.getCustomer() != null) {
            dto.setCustomer(customerMapper.toResponseDto(pointTransaction.getCustomer()));
        } else {
            dto.setCustomer(null);
        }
        // Chỉ map ID của Invoice, không cần DTO đầy đủ để tránh vòng lặp
        if (pointTransaction.getInvoice() != null) {
            dto.setInvoiceId(pointTransaction.getInvoice().getId());
        } else {
            dto.setInvoiceId(null);
        }

        return dto;
    }

    // Mapping từ DTO sang Entity cho PointTransaction thường không cần vì nó được tạo nội bộ bởi Service
    // Nếu có Request DTO và cần, bạn sẽ tự code tương tự.
}