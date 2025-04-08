package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO dto);
    CategoryDTO getCategoryById(Integer id);
    List<CategoryDTO> getAllCategories();
    CategoryDTO updateCategory(Integer id, CategoryDTO dto);
    void deleteCategory(Integer id);
}
