package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    // Lấy tất cả giao dịch điểm của một khách hàng, sắp xếp theo ngày
    List<PointTransaction> findByCustomerIdOrderByTransactionDateDesc(Long customerId);

    // Lấy giao dịch điểm liên quan đến một hóa đơn cụ thể
    Optional<PointTransaction> findByInvoiceId(Long invoiceId);
}