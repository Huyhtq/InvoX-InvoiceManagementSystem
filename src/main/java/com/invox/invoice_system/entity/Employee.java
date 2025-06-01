package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.EmployeeStatus; // Import Enum EmployeeStatus
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Employee", uniqueConstraints = {
    @UniqueConstraint(columnNames = "phone"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "employee_id_seq_gen")
    @SequenceGenerator(
        name = "employee_id_seq_gen",
        sequenceName = "Employee_SEQ",
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

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate = LocalDate.now(); // Mặc định là ngày hiện tại

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @Column(name = "is_active", nullable = false, length = 10) // Tên cột trong DB
    private EmployeeStatus status = EmployeeStatus.ACTIVE; // Mặc định là ACTIVE

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AppUser appUser;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private java.util.List<Invoice> invoices;
}