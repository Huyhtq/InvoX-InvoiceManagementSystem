package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "InvoiceDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "invoicedetail_id_seq_gen")
    @SequenceGenerator(
        name = "invoicedetail_id_seq_gen",
        sequenceName = "InvoiceDetail_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice; // Giá bán của sản phẩm tại thời điểm bán (snapshot)

    @Column(name = "product_name_snapshot", nullable = false, length = 100)
    private String productNameSnapshot; // Tên sản phẩm tại thời điểm bán (snapshot)

    @Column(name = "sub_total", nullable = false)
    private Long subTotal; // Thành tiền cho dòng này (quantity * unitPrice)
}