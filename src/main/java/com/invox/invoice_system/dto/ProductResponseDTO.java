package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String sku;
    private String name;
    private Long price;
    private Long costPrice;
    private Long quantity;
    private CategoryDTO category; // Trả về Category dưới dạng DTO
    private String brand;
    private String imageUrl;
    private String description;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}