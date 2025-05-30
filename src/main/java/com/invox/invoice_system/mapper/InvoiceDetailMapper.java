package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.InvoiceDetail;
import com.invox.invoice_system.entity.Product; // Import for mapping ID
import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ProductMapper.class}) // uses ProductMapper for nested product DTO
public interface InvoiceDetailMapper {

    InvoiceDetailMapper INSTANCE = Mappers.getMapper(InvoiceDetailMapper.class);

    @Mapping(source = "product", target = "product") // Map product entity to product response DTO
    InvoiceDetailResponseDTO toResponseDto(InvoiceDetail invoiceDetail);

    @Mapping(source = "productId", target = "product.id") // Map product ID from DTO to product entity ID
    @Mapping(target = "id", ignore = true) // Ignore ID when creating new entity
    @Mapping(target = "invoice", ignore = true) // Invoice will be set by service
    @Mapping(target = "unitPrice", ignore = true) // Calculated/set by service
    @Mapping(target = "productNameSnapshot", ignore = true) // Set by service
    @Mapping(target = "subTotal", ignore = true) // Calculated by service
    InvoiceDetail toEntity(InvoiceDetailRequestDTO requestDTO);

    // Helper for mapping Product ID to Product entity
    default Product toProductEntity(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
