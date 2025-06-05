package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.InvoiceDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long>{
    List<InvoiceDetail> findAllByInvoiceIn(List<Invoice> invoices);
}
