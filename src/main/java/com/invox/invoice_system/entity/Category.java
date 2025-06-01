package com.invox.invoice_system.entity;

import jakarta.persistence.*;
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

    @Column(name = "description", length = 255)
    private String description;

    // mappedBy trỏ đến tên trường Category trong lớp Product
    // CascadeType.ALL: mọi thao tác trên Category sẽ ảnh hưởng đến Product liên quan
    // orphanRemoval = true: loại bỏ Product nếu Category bị xóa
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Product> products; // Sử dụng java.util.List để tránh xung đột tên
}