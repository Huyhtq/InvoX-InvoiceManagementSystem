package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.CategoryDTO;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.mapper.CategoryMapper;
import com.invox.invoice_system.repository.CategoryRepository;
import com.invox.invoice_system.service.CategoryService; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service 
@RequiredArgsConstructor 
public class CategoryServiceImpl implements CategoryService { 

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true) // Đánh dấu giao dịch chỉ đọc
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto);
    }

    @Override
    @Transactional // Đánh dấu giao dịch ghi
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Ví dụ: Kiểm tra tên danh mục duy nhất
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryDTO.getName());
        }
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra tên duy nhất khi cập nhật (ngoại trừ chính danh mục đang cập nhật)
        Optional<Category> categoryWithName = categoryRepository.findByName(categoryDTO.getName());
        if (categoryWithName.isPresent() && !categoryWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryDTO.getName());
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có sản phẩm liên quan trước khi xóa
        // if (productRepository.countByCategoryId(id) > 0) {
        //     throw new IllegalStateException("Không thể xóa danh mục khi còn sản phẩm liên quan.");
        // }
        categoryRepository.deleteById(id);
    }
}