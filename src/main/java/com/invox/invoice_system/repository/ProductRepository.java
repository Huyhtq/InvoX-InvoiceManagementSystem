package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm sản phẩm theo tên chính xác (thường dùng để kiểm tra trùng lặp)
    Optional<Product> findByName(String name);

    // Tìm sản phẩm theo tên chứa một chuỗi (tìm kiếm gần đúng)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm sản phẩm theo ID danh mục
    List<Product> findByCategoryId(Long categoryId);

    // Tìm sản phẩm theo trạng thái (ví dụ: đang kinh doanh, hết hàng)
    List<Product> findByStatus(ProductStatus status);

    // Tìm sản phẩm có số lượng tồn kho dưới một ngưỡng nhất định (ví dụ: để đặt hàng lại)
    List<Product> findByQuantityLessThan(Long quantity);

    //
    List<Product> findByCategory_IdAndStatus(Long categoryId, ProductStatus status);

    // Tìm chính xác theo SKU
    Optional<Product> findBySku(String sku); 

    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String nameTerm, String skuTerm);

    Integer countByCategoryId(Long id);

    long countByCategory(Category category);
}