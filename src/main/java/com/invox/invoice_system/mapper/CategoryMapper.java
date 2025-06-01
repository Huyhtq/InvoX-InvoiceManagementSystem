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
        entity.setDescription(categoryDTO.getDescription());
        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Entity hiện có
    public void updateEntityFromDto(CategoryDTO categoryDTO, Category category) {
        if (categoryDTO == null || category == null) {
            return;
        }
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
    }
}