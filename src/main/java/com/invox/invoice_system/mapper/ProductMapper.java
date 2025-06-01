package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper; // Inject CategoryMapper

    // Convert Product Entity to ProductResponseDTO
    public ProductResponseDTO toResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice()); // Đảm bảo kiểu BigDecimal/Long khớp
        dto.setCostPrice(product.getCostPrice()); // Đảm bảo kiểu BigDecimal/Long khớp
        dto.setQuantity(product.getQuantity());

        if (product.getCategory() != null) {
            dto.setCategory(categoryMapper.toDto(product.getCategory())); // Gọi CategoryMapper
        }

        dto.setBrand(product.getBrand());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus()); // Enum ProductStatus
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    // Convert ProductRequestDTO to Product Entity (khi tạo mới)
    public Product toEntity(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            return null;
        }

        Product entity = new Product();
        entity.setId(productRequestDTO.getId()); // <-- Gán ID từ DTO
        entity.setSku(productRequestDTO.getSku());
        entity.setName(productRequestDTO.getName());
        entity.setPrice(productRequestDTO.getPrice()); // Đảm bảo kiểu BigDecimal/Long khớp
        entity.setCostPrice(productRequestDTO.getCostPrice()); // Đảm bảo kiểu BigDecimal/Long khớp
        entity.setQuantity(productRequestDTO.getQuantity());
        entity.setBrand(productRequestDTO.getBrand());
        entity.setImageUrl(productRequestDTO.getImageUrl());
        entity.setDescription(productRequestDTO.getDescription());
        entity.setStatus(productRequestDTO.getStatus()); // Enum ProductStatus

        // Category sẽ được Service tìm và set dựa trên categoryId
        // entity.setCategory(categoryMapper.toEntity(productRequestDTO.getCategoryId())); // Đây là helper
        return entity;
    }

    // Convert ProductResponseDTO to Product Entity (dùng cho InvoiceServiceImpl)
    public Product toEntity(ProductResponseDTO productResponseDTO) {
        if (productResponseDTO == null) {
            return null;
        }

        Product entity = new Product();
        entity.setId(productResponseDTO.getId());
        entity.setSku(productResponseDTO.getSku());
        entity.setName(productResponseDTO.getName());
        entity.setPrice(productResponseDTO.getPrice());
        entity.setCostPrice(productResponseDTO.getCostPrice());
        entity.setQuantity(productResponseDTO.getQuantity());
        entity.setBrand(productResponseDTO.getBrand());
        entity.setImageUrl(productResponseDTO.getImageUrl());
        entity.setDescription(productResponseDTO.getDescription());
        entity.setStatus(productResponseDTO.getStatus());

        // Category Entity sẽ được Service hoặc JPA quản lý hoặc tải lại
        // hoặc bạn có thể gọi categoryMapper.toEntity(productResponseDTO.getCategory()) nếu muốn
        return entity;
    }

    // Helper method to map Category ID to Category entity (nếu cần trong Service)
    public Category toCategoryEntity(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}