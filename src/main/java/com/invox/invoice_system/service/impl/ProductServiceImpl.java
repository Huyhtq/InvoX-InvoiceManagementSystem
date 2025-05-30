package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.enums.ProductStatus;
import com.invox.invoice_system.mapper.ProductMapper;
import com.invox.invoice_system.repository.CategoryRepository;
import com.invox.invoice_system.repository.ProductRepository;
import com.invox.invoice_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Cần để kiểm tra categoryId
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        // Kiểm tra tên sản phẩm duy nhất
        if (productRepository.findByName(productRequestDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại: " + productRequestDTO.getName());
        }

        // Kiểm tra categoryId tồn tại
        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + productRequestDTO.getCategoryId()));

        Product product = productMapper.toEntity(productRequestDTO);
        product.setCategory(category); // Gán đối tượng Category cho Product

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));

        // Kiểm tra tên sản phẩm duy nhất khi cập nhật (trừ chính nó)
        Optional<Product> productWithName = productRepository.findByName(productRequestDTO.getName());
        if (productWithName.isPresent() && !productWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại: " + productRequestDTO.getName());
        }

        // Kiểm tra categoryId tồn tại
        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + productRequestDTO.getCategoryId()));

        // Cập nhật các trường từ DTO vào Entity
        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setPrice(productRequestDTO.getPrice());
        existingProduct.setCostPrice(productRequestDTO.getCostPrice());
        existingProduct.setQuantity(productRequestDTO.getQuantity());
        existingProduct.setBrand(productRequestDTO.getBrand());
        existingProduct.setImageUrl(productRequestDTO.getImageUrl());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setStatus(productRequestDTO.getStatus());
        existingProduct.setCategory(category); // Cập nhật Category

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProducts(String searchTerm, ProductStatus status, Long categoryId) {
        List<Product> products;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(searchTerm);
        } else if (status != null && categoryId != null) {
            products = productRepository.findByCategory_IdAndStatus(categoryId, status);
        } else if (status != null) {
            products = productRepository.findByStatus(status);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }
        return products.stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProductQuantity(Long productId, Long quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        Long newQuantity = product.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không thể âm.");
        }
        product.setQuantity(newQuantity);

        // Cập nhật trạng thái sản phẩm nếu hết hàng
        if (newQuantity == 0) {
            product.setStatus(ProductStatus.OOS); // Out of Stock
        } else if (product.getStatus() == ProductStatus.OOS && newQuantity > 0) {
            product.setStatus(ProductStatus.ACTIVE); // Trở lại Active nếu có hàng
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }
}