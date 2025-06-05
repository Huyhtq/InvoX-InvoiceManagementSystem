package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.CategoryDTO;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.mapper.CategoryMapper;
import com.invox.invoice_system.repository.CategoryRepository;
import com.invox.invoice_system.repository.ProductRepository;
import com.invox.invoice_system.service.CategoryService; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service 
@RequiredArgsConstructor 
public class CategoryServiceImpl implements CategoryService { 

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
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

    private void validateCategoryCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("Mã danh mục (code) không được để trống.");
        }
        if (code.length() != 3) {
            throw new IllegalArgumentException("Mã danh mục (code) phải có đúng 3 ký tự.");
        }
        // Có thể thêm kiểm tra ký tự (ví dụ: chỉ chấp nhận chữ cái viết hoa)
        if (!code.matches("^[A-Z]+$")) { // Ví dụ: Chỉ chấp nhận A-Z
             throw new IllegalArgumentException("Mã danh mục (code) chỉ được chứa chữ cái viết hoa.");
        }
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        validateCategoryCode(categoryDTO.getCode()); // Validate code
        String upperCaseCode = categoryDTO.getCode().toUpperCase();
        categoryDTO.setCode(upperCaseCode);


        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryDTO.getName());
        }
        if (categoryRepository.findByCode(upperCaseCode).isPresent()) {
            throw new IllegalArgumentException("Mã danh mục (code) đã tồn tại: " + upperCaseCode);
        }

        Category category = categoryMapper.toEntity(categoryDTO);
        if (category.getTotal() == null) { // Đảm bảo total có giá trị mặc định
            category.setTotal(0);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        // Validate và chuẩn hóa code nếu có thay đổi
        if (categoryDTO.getCode() != null && !categoryDTO.getCode().equalsIgnoreCase(existingCategory.getCode())) {
            validateCategoryCode(categoryDTO.getCode());
            String upperCaseCode = categoryDTO.getCode().toUpperCase();
            categoryDTO.setCode(upperCaseCode);
            if (categoryRepository.findByCode(upperCaseCode).filter(cat -> !cat.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Mã danh mục (code) đã tồn tại: " + upperCaseCode);
            }
             existingCategory.setCode(upperCaseCode);
        }


        Optional<Category> categoryWithName = categoryRepository.findByName(categoryDTO.getName());
        if (categoryWithName.isPresent() && !categoryWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryDTO.getName());
        }

        // Cập nhật các trường khác
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        if (categoryDTO.getTotal() != null) { // Cho phép cập nhật total nếu được gửi lên
            existingCategory.setTotal(categoryDTO.getTotal());
        }


        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra nếu có sản phẩm liên quan trước khi xóa
        // Giả sử entity Product có trường 'category' và CategoryRepository có phương thức countProductsByCategoryId
        // Hoặc bạn có thể inject ProductRepository và gọi productRepository.countByCategory(category)
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
             throw new IllegalStateException("Không thể xóa danh mục '" + category.getName() + "' khi còn sản phẩm liên quan.");
        }
        // Hoặc nếu bạn dùng ProductRepository:
        if (productRepository.countByCategoryId(id) > 0) { // Cần thêm method này vào ProductRepository
            throw new IllegalStateException("Không thể xóa danh mục khi còn sản phẩm liên quan.");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getCategoryCodeById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Integer> getCategoryTotalProducts(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getTotal);
    }

    @Override
    @Transactional
    public void incrementCategoryTotal(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + categoryId + " để tăng total."));
        category.setTotal(category.getTotal() + 1);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void decrementCategoryTotal(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + categoryId + " để giảm total."));
        if (category.getTotal() > 0) {
            category.setTotal(category.getTotal() - 1);
            categoryRepository.save(category);
        }
    }
}