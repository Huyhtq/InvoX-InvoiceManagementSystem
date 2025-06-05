
package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.entity.InvoiceDetail;
import com.invox.invoice_system.enums.PaymentMethod;
import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final CustomerMapper customerMapper;
    private final EmployeeMapper employeeMapper;
    private final InvoiceDetailMapper invoiceDetailMapper; // Inject InvoiceDetailMapper

    // Convert Invoice Entity to InvoiceResponseDTO
    public InvoiceResponseDTO toResponseDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setTotalAmount(invoice.getTotalAmount()); // Đảm bảo kiểu Long/BigDecimal khớp
        dto.setDiscountAmount(invoice.getDiscountAmount()); // Đảm bảo kiểu Long/BigDecimal khớp
        dto.setPointsRedeemed(invoice.getPointsRedeemed());
        dto.setFinalAmount(invoice.getFinalAmount()); // Đảm bảo kiểu Long/BigDecimal khớp
        dto.setPaymentMethod(invoice.getPaymentMethod()); // Enum PaymentMethod
        dto.setStatus(invoice.getStatus()); // Enum InvoiceStatus
        dto.setNotes(invoice.getNotes());
        dto.setUpdatedAt(invoice.getUpdatedAt());

        if (invoice.getCustomer() != null) {
            dto.setCustomer(customerMapper.toResponseDto(invoice.getCustomer()));
        } else {
            dto.setCustomer(null);
        }
        if (invoice.getEmployee() != null) {
            dto.setEmployee(employeeMapper.toResponseDto(invoice.getEmployee()));
        } else {
            dto.setEmployee(null);
        }

        // Ánh xạ danh sách InvoiceDetail Entities sang InvoiceDetailResponseDTOs
        if (invoice.getInvoiceDetails() != null) {
            dto.setInvoiceDetails(invoice.getInvoiceDetails().stream()
                    .map(invoiceDetailMapper::toResponseDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setInvoiceDetails(Collections.emptyList());
        }

        return dto;
    }

    // Convert InvoiceCreationRequestDTO to Invoice Entity (khi tạo mới)
    // Lưu ý: Các trường tính toán (totalAmount, finalAmount, etc.) sẽ được Service set
    public Invoice toEntity(InvoiceCreationRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Invoice entity = new Invoice();
        // ID, invoiceDate, totalAmount, discountAmount, pointsRedeemed, finalAmount, status, updatedAt, pointTransactions
        // sẽ được Service hoặc JPA quản lý/set
        entity.setNotes(requestDTO.getNotes());
        
        // Chuyển String sang Enum PaymentMethod
        if (requestDTO.getPaymentMethod() != null && !requestDTO.getPaymentMethod().isEmpty()) {
            try {
                entity.setPaymentMethod(PaymentMethod.valueOf(requestDTO.getPaymentMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Xử lý nếu string không khớp với enum (ví dụ: log lỗi, hoặc mặc định)
                entity.setPaymentMethod(null); // Hoặc một giá trị mặc định
            }
        } else {
            entity.setPaymentMethod(null);
        }

        // Customer và Employee sẽ được Service tìm và set dựa trên ID
        if (requestDTO.getCustomerId() != null) {
            Customer customerRef = new Customer();
            customerRef.setId(requestDTO.getCustomerId());
            entity.setCustomer(customerRef);
        } else {
            entity.setCustomer(null);
        }

        // Ánh xạ danh sách InvoiceDetailRequestDTOs sang InvoiceDetail Entities (không set Invoice cho detail ở đây)
        if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
            List<InvoiceDetail> invoiceDetails = requestDTO.getItems().stream()
                    .map(invoiceDetailMapper::toEntity) // Gọi InvoiceDetailMapper
                    .collect(Collectors.toList());
            
            // Quan trọng: Gán lại Invoice cho mỗi InvoiceDetail
            invoiceDetails.forEach(detail -> detail.setInvoice(entity));
            entity.setInvoiceDetails(invoiceDetails);
        } else {
            entity.setInvoiceDetails(Collections.emptyList());
        }

        return entity;
    }
}