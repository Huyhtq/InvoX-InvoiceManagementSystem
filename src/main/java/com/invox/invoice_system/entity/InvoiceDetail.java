package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "InvoiceDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Long unitPrice;
}
