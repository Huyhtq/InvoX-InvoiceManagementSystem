package com.invox.invoice_system.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Long price;
    private Integer quantity;
    private Integer categoryId;
    private String categoryName;
}
