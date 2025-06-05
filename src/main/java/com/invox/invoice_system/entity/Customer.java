package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.Gender; // Import Enum Gender
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customer", uniqueConstraints = {
    @UniqueConstraint(columnNames = "phone"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "customer_id_seq_gen")
    @SequenceGenerator(
        name = "customer_id_seq_gen",
        sequenceName = "Customer_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate; // DATE trong DB

    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints = 0L; // Mặc định 0L để khớp với DB default 0

    @Column(name = "available_points", nullable = false)
    private Long availablePoints = 0L; // Mặc định 0L

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading cho MemberRank
    @JoinColumn(name = "member_rank_id", nullable = false) // Tên cột FK trong bảng Customer
    private MemberRank memberRank;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // TIMESTAMP trong DB

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // TIMESTAMP trong DB

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private java.util.List<Invoice> invoices;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private java.util.List<PointTransaction> pointTransactions;
}