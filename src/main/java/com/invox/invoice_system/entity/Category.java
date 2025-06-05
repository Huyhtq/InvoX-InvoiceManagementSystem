package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Category", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "category_id_seq_gen") 
    @SequenceGenerator(
        name = "category_id_seq_gen",
        sequenceName = "Category_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(min = 3, max = 3, message = "Mã danh mục phải có đúng 3 ký tự")
    @Column(length = 3, nullable = false, unique = true)
    private String code;

    private Integer total; // tổng số mặt hàng loại đó được đánh số 
    
    @Column(name = "description", length = 255)
    private String description;

    // mappedBy trỏ đến tên trường Category trong lớp Product
    // CascadeType.ALL: mọi thao tác trên Category sẽ ảnh hưởng đến Product liên quan
    // orphanRemoval = true: loại bỏ Product nếu Category bị xóa
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Product> products; // Sử dụng java.util.List để tránh xung đột tên
}