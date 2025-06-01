package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import com.invox.invoice_system.service.PointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/point-transactions")
@RequiredArgsConstructor
public class PointTransactionApiController {

    private final PointTransactionService pointTransactionService;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PointTransactionResponseDTO>> getPointTransactionsByCustomerId(@PathVariable Long customerId) {
        List<PointTransactionResponseDTO> transactions = pointTransactionService.getPointTransactionsByCustomerId(customerId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Các phương thức tạo/cập nhật PointTransaction thường không được expose qua API
    // mà được gọi nội bộ từ các Service khác (ví dụ: InvoiceService, CustomerService).
}