package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.*;
import com.invox.invoice_system.entity.*;
import com.invox.invoice_system.repository.*;
import com.invox.invoice_system.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .category(category)
                .build();
        return mapToDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id).orElseThrow();
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(category);
        return mapToDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
