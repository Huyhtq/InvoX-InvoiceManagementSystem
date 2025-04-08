package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    private Long id;

    private String name;
    private String phone;
    private String email;
    
    @Column(name = "total_points")
    private Integer totalPoints;

    @Column(name = "available_points")
    private Integer availablePoints;
}