package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO dto);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);
    void deleteProduct(Long id);
}
