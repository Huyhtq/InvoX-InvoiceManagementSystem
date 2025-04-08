package com.invox.invoice_system.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    private String name;
    private Long price;
    private Integer quantity;
    private Integer categoryId;
}
