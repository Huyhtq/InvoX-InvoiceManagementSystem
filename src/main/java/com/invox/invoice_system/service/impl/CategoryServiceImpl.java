package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.CategoryDTO;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.repository.CategoryRepository;
import com.invox.invoice_system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public CategoryDTO updateCategory(Integer id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getName());
        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }
}
