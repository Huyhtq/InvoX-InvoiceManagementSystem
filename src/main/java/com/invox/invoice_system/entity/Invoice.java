package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.InvoiceStatus; // Import Enum InvoiceStatus
import com.invox.invoice_system.enums.PaymentMethod; // Import Enum PaymentMethod
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hoặc GenerationType.SEQUENCE cho Oracle
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @CreationTimestamp
    @Column(name = "invoice_date", nullable = false, updatable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount; // Tổng tiền trước giảm giá và trừ điểm

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount = 0.0; // Số tiền giảm giá

    @Column(name = "points_redeemed", nullable = false)
    private Long pointsRedeemed = 0L; // Số điểm đã dùng

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount; // Tổng tiền cuối cùng khách phải trả

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.COMPLETED; // Mặc định là COMPLETED

    @Column(name = "notes", length = 255)
    private String notes;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<InvoiceDetail> invoiceDetails;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private java.util.List<PointTransaction> pointTransactions;
}