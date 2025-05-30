package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.enums.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();
    Optional<ProductResponseDTO> getProductById(Long id);
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
    List<ProductResponseDTO> searchProducts(String searchTerm, ProductStatus status, Long categoryId);
    ProductResponseDTO updateProductQuantity(Long productId, Long quantityChange);
}