package com.invox.invoice_system.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "MemberRank", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRank {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "memberrank_id_seq_gen")
    @SequenceGenerator(
        name = "memberrank_id_seq_gen",
        sequenceName = "MemberRank_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "min_total_points", nullable = false)
    private Long minTotalPoints; // Sử dụng Long để tương ứng với NUMBER(8,0)

    @Column(name = "points_earning_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal pointsEarningRate; // DECIMAL(5,2) trong DB

    @Column(name = "description", length = 255)
    private String description;

    // Quan hệ One-to-Many với Customer
    @OneToMany(mappedBy = "memberRank", cascade = CascadeType.ALL)
    private java.util.List<Customer> customers;
}