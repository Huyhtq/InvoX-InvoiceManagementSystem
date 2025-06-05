package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.enums.ProductStatus;
import com.invox.invoice_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductApiController.class);

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Long categoryId) {
        List<ProductResponseDTO> products = productService.searchProducts(searchTerm, status, categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/update-quantity")
    public ResponseEntity<?> updateProductQuantity(@PathVariable Long id, @RequestParam Long quantityChange) {
        ProductResponseDTO updatedProduct = productService.updateProductQuantity(id, quantityChange);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @GetMapping("/suggest-sku")
    public ResponseEntity<String> getSuggestedSku(@RequestParam("categoryId") Long categoryId) {
        try {
            logger.info("API: Received request to suggest SKU for categoryId: {}", categoryId);
            String suggestedSku = productService.suggestSkuForCategory(categoryId);
            logger.info("API: Suggested SKU for categoryId {}: {}", categoryId, suggestedSku);
            return ResponseEntity.ok(suggestedSku);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("API: Bad request for SKU suggestion (categoryId {}): {}", categoryId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("API: Internal server error suggesting SKU for categoryId {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi tạo SKU.");
        }
    }
}