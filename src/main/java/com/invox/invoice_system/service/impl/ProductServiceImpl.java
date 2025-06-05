package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.enums.ProductStatus;
import com.invox.invoice_system.mapper.ProductMapper;
import com.invox.invoice_system.repository.CategoryRepository;
import com.invox.invoice_system.repository.ProductRepository;
import com.invox.invoice_system.service.CategoryService;
import com.invox.invoice_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Cần để kiểm tra categoryId
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
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

        if (productRequestDTO.getSku() != null && productRepository.findBySku(productRequestDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("Mã SKU sản phẩm đã tồn tại: " + productRequestDTO.getSku());
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

        if (productRequestDTO.getSku() != null) {
            Optional<Product> productWithSku = productRepository.findBySku(productRequestDTO.getSku());
            if (productWithSku.isPresent() && !productWithSku.get().getId().equals(id)) {
                throw new IllegalArgumentException("Mã SKU sản phẩm đã tồn tại: " + productRequestDTO.getSku());
            }
        }

        // Kiểm tra categoryId tồn tại
        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + productRequestDTO.getCategoryId()));

        // Cập nhật các trường từ DTO vào Entity
        existingProduct.setSku(productRequestDTO.getSku());
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

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProductsByTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.findAll().stream().map(productMapper::toResponseDto).collect(Collectors.toList());
        }
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(searchTerm, searchTerm).stream()
            .map(productMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String suggestSkuForCategory(Long categoryId) {
        logger.info("Attempting to suggest SKU for categoryId: {}", categoryId);
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID không được để trống để tạo SKU.");
        }

        // Lấy mã code của Category từ CategoryService
        String categoryCode = categoryService.getCategoryCodeById(categoryId)
                .filter(StringUtils::hasText) // Đảm bảo code không rỗng
                .orElseThrow(() -> {
                    logger.warn("Không thể tìm thấy mã code hợp lệ cho category ID: {}", categoryId);
                    return new IllegalArgumentException("Không tìm thấy mã code hợp lệ cho danh mục đã chọn (ID: " + categoryId + ")");
                });

        logger.debug("Category code for ID {}: {}", categoryId, categoryCode);

        // Lựa chọn logic tính số thứ tự tiếp theo:

        // === LỰA CHỌN 1: Dựa vào trường 'total' trong Category (nếu bạn duy trì nó) ===
        // int currentTotal = categoryService.getCategoryTotalProducts(categoryId).orElse(0);
        // long nextSkuNumber = currentTotal + 1;
        // logger.debug("Next SKU number based on category total ({}): {}", currentTotal, nextSkuNumber);

        // === LỰA CHỌN 2: Tính động dựa trên SKU lớn nhất hiện có (khuyến nghị nếu 'total' khó duy trì chính xác) ===
        String upperCategoryCode = categoryCode.toUpperCase();
        long currentMaxSkuNumber = productRepository.findAll().stream()
            .map(Product::getSku)
            .filter(sku -> sku != null && sku.toUpperCase().startsWith(upperCategoryCode))
            .map(sku -> sku.substring(upperCategoryCode.length())) // Lấy phần số ví dụ "001", "006"
            .filter(numStr -> numStr.matches("\\d+"))      // Chỉ lấy các chuỗi là số
            .mapToLong(Long::parseLong)                           // Chuyển sang long
            .max()                                                // Tìm số lớn nhất
            .orElse(0L);                                      // Nếu chưa có sản phẩm nào, bắt đầu từ 0
        long nextSkuNumber = currentMaxSkuNumber + 1;
        logger.debug("Next SKU number based on dynamic count (max existing {}): {}", currentMaxSkuNumber, nextSkuNumber);
        // === KẾT THÚC LỰA CHỌN 2 ===


        // Định dạng số thứ tự thành 3 chữ số, ví dụ: 7 -> "007", 12 -> "012", 123 -> "123"
        String formattedSkuNumber = String.format("%03d", nextSkuNumber);
        String suggestedSku = upperCategoryCode + formattedSkuNumber;
        logger.info("Suggested SKU for categoryId {}: {}", categoryId, suggestedSku);

        return suggestedSku;
    }
}