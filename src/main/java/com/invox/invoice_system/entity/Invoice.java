package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "Invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "created_date")
    @Temporal(TemporalType.DATE)
    private Date date;

    private Long total;
}
