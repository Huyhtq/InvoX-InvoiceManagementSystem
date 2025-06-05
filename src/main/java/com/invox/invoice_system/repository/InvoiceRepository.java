package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Tìm tất cả hóa đơn của một khách hàng cụ thể
    List<Invoice> findByCustomerId(Long customerId);

    // Tìm tất cả hóa đơn được tạo bởi một nhân viên cụ thể
    List<Invoice> findByEmployeeId(Long employeeId);

    // Tìm hóa đơn trong một khoảng thời gian
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm hóa đơn theo trạng thái
    List<Invoice> findByStatus(InvoiceStatus status);

    // Lấy tất cả hóa đơn đã HOÀN THÀNH trong một khoảng thời gian
    List<Invoice> findAllByStatusAndInvoiceDateBetween(InvoiceStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Ví dụ truy vấn tính tổng totalAmount (doanh thu thô)
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = :status AND i.invoiceDate BETWEEN :startDate AND :endDate")
    Long sumTotalAmountByStatusAndInvoiceDateBetween(
        @Param("status") InvoiceStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}