package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.ProductStatus; // Import Enum ProductStatus
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "product_id_seq_gen") 
    @SequenceGenerator(
        name = "product_id_seq_gen",
        sequenceName = "Product_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "sku", nullable = false, unique = true, length = 50) // Ánh xạ với cột SKU trong DB
    private String sku;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price; // Sử dụng Double cho giá

    @Column(name = "cost_price")
    private Long costPrice; // Giá nhập

    @Column(name = "quantity", nullable = false)
    private Long quantity; // Số lượng tồn kho

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading cho Category
    @JoinColumn(name = "category_id") // Tên cột FK trong bảng Product
    private Category category;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "description", columnDefinition = "CLOB") // CLOB trong Oracle
    private String description;

    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE; // Mặc định là ACTIVE

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private java.util.List<InvoiceDetail> invoiceDetails;
}