package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.dto.CategoryDTO;
import org.springframework.stereotype.Component; // Quan trọng để Spring quản lý nó

@Component // Đánh dấu là Spring Component để có thể @Autowired
public class CategoryMapper {

    public CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCode(category.getCode());
        dto.setTotal(category.getTotal());
        dto.setDescription(category.getDescription());
        return dto;
    }

    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        Category entity = new Category();
        entity.setId(categoryDTO.getId()); // ID có thể null khi tạo mới
        entity.setName(categoryDTO.getName());
        entity.setCode(categoryDTO.getCode() != null ? categoryDTO.getCode().toUpperCase() : null); 
        entity.setTotal(categoryDTO.getTotal() != null ? categoryDTO.getTotal() : 0);
        entity.setDescription(categoryDTO.getDescription());
        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Entity hiện có
    public void updateEntityFromDto(CategoryDTO categoryDTO, Category category) {
        if (categoryDTO == null || category == null) {
            return;
        }
        category.setName(categoryDTO.getName());
        if (categoryDTO.getCode() != null) { // Chỉ cập nhật code nếu được cung cấp và hợp lệ
            if (categoryDTO.getCode().length() == 3) {
                category.setCode(categoryDTO.getCode().toUpperCase());
            } else {
                // Có thể throw exception hoặc bỏ qua nếu code không hợp lệ
                // throw new IllegalArgumentException("Mã danh mục phải có 3 ký tự.");
            }
        }
        if (categoryDTO.getTotal() != null) {
            category.setTotal(categoryDTO.getTotal());
        }
        category.setDescription(categoryDTO.getDescription());
    }
}