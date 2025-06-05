package com.invox.invoice_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(min = 3, max = 3, message = "Mã danh mục phải có đúng 3 ký tự")
    private String code; // Mã danh mục (ví dụ: "BEV")

    private Integer total;
    
    private String description;
}