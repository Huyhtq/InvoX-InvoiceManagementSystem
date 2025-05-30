package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.PointTransactionType; // Import Enum PointTransactionType
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PointTransaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hoặc GenerationType.SEQUENCE cho Oracle
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id") // invoice_id có thể null
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private PointTransactionType transactionType;

    @Column(name = "points_amount", nullable = false)
    private Long pointsAmount; // Số điểm thay đổi (dương cho EARN, âm cho REDEEM)

    @Column(name = "current_total_points")
    private Long currentTotalPoints; // Tổng điểm của khách hàng sau giao dịch này

    @Column(name = "current_available_points")
    private Long currentAvailablePoints; // Điểm khả dụng sau giao dịch này

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_by", length = 50)
    private String createdBy; // Ai đã thực hiện giao dịch (username hoặc tên nhân viên)
}