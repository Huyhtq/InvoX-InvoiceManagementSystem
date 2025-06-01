package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.InvoiceDetail;
import com.invox.invoice_system.entity.Product; // Import for mapping ID

import lombok.RequiredArgsConstructor;

import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailResponseDTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceDetailMapper {

    private final ProductMapper productMapper; // Inject ProductMapper

    // Convert InvoiceDetail Entity to InvoiceDetailResponseDTO
    public InvoiceDetailResponseDTO toResponseDto(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null) {
            return null;
        }

        InvoiceDetailResponseDTO dto = new InvoiceDetailResponseDTO();
        dto.setId(invoiceDetail.getId());
        dto.setQuantity(invoiceDetail.getQuantity());
        // Đảm bảo kiểu dữ liệu khớp: Long hoặc BigDecimal
        dto.setUnitPrice(invoiceDetail.getUnitPrice());
        dto.setProductNameSnapshot(invoiceDetail.getProductNameSnapshot());
        // Đảm bảo kiểu dữ liệu khớp: Long hoặc BigDecimal
        dto.setSubTotal(invoiceDetail.getSubTotal());

        // Ánh xạ Product Entity sang ProductResponseDTO (có thể là DTO rút gọn)
        if (invoiceDetail.getProduct() != null) {
            dto.setProduct(productMapper.toResponseDto(invoiceDetail.getProduct()));
        } else {
            dto.setProduct(null);
        }
        // Không ánh xạ Invoice ở đây để tránh vòng lặp vô hạn

        return dto;
    }

    // Convert InvoiceDetailRequestDTO to InvoiceDetail Entity (khi tạo mới)
    public InvoiceDetail toEntity(InvoiceDetailRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        InvoiceDetail entity = new InvoiceDetail();
        // ID, Invoice, unitPrice, productNameSnapshot, subTotal sẽ được Service hoặc JPA quản lý/set
        entity.setQuantity(requestDTO.getQuantity());

        // Product sẽ được Service tìm và set dựa trên productId. Mapper này chỉ cần tạo một Product "tham chiếu".
        if (requestDTO.getProductId() != null) {
            Product productRef = new Product();
            productRef.setId(requestDTO.getProductId());
            entity.setProduct(productRef);
        } else {
            entity.setProduct(null);
        }

        return entity;
    }
}
