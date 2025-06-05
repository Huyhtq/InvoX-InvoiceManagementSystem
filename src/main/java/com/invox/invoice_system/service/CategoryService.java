package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.CategoryDTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    Optional<CategoryDTO> getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    Optional<String> getCategoryCodeById(Long categoryId);
    Optional<Integer> getCategoryTotalProducts(Long categoryId);
    void incrementCategoryTotal(Long categoryId);
    void decrementCategoryTotal(Long categoryId);
}