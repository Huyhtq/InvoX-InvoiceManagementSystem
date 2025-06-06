package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private Long id;
    private String sku;
    private String name;
    private Long price;
    private Long costPrice;
    private Long quantity;
    private Long categoryId; // Chỉ cần ID để liên kết
    private String brand;
    private String imageUrl;
    private String description;
    private ProductStatus status;
}