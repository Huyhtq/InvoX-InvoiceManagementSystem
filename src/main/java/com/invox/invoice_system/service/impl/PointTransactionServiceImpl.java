package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.PointTransaction;
import com.invox.invoice_system.enums.PointTransactionType;
import com.invox.invoice_system.mapper.PointTransactionMapper;
import com.invox.invoice_system.repository.PointTransactionRepository;
import com.invox.invoice_system.service.PointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointTransactionServiceImpl implements PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;
    private final PointTransactionMapper pointTransactionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PointTransactionResponseDTO> getPointTransactionsByCustomerId(Long customerId) {
        return pointTransactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId).stream()
                .map(pointTransactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    // Sử dụng Propagation.REQUIRES_NEW để đảm bảo giao dịch này luôn được ghi,
    // ngay cả khi giao dịch chính (ví dụ: tạo hóa đơn) bị rollback.
    // Hoặc nếu không cần mức độ đảm bảo cao như vậy, có thể chỉ để @Transactional mặc định.
    @Transactional
    public void createPointTransaction(Customer customer, Invoice invoice, PointTransactionType type, Long pointsAmount, String description) {
        PointTransaction transaction = new PointTransaction();
        transaction.setCustomer(customer);
        transaction.setInvoice(invoice);
        transaction.setTransactionType(type);
        transaction.setPointsAmount(pointsAmount); // Số điểm thay đổi (dương nếu tích, âm nếu đổi)
        transaction.setDescription(description);

        // Cập nhật snapshot của tổng điểm và điểm khả dụng tại thời điểm giao dịch
        // Lấy lại customer từ DB để đảm bảo dữ liệu mới nhất (nếu có thể)
        // Tuy nhiên, nếu phương thức này được gọi trong cùng transaction với customerService.addPointsToCustomer,
        // thì customer object đã được cập nhật
        transaction.setCurrentTotalPoints(customer.getTotalPoints());
        transaction.setCurrentAvailablePoints(customer.getAvailablePoints());

        pointTransactionRepository.save(transaction);
    }
}