package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "AppUser", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "appuser_id_seq_gen") 
    @SequenceGenerator(
        name = "appuser_id_seq_gen",
        sequenceName = "AppUser_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Lưu mật khẩu đã băm

    @OneToOne(fetch = FetchType.LAZY) // Lazy loading cho Employee
    @JoinColumn(name = "employee_id", unique = true) // Cột FK và duy nhất
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER) // Eager loading cho Role vì thường cần vai trò ngay
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<History> histories;
}