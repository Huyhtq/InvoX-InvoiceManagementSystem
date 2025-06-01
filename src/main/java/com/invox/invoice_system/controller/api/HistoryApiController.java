package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryApiController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getAllHistoryRecords() {
        List<HistoryResponseDTO> historyRecords = historyService.getAllHistoryRecords();
        return new ResponseEntity<>(historyRecords, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoryResponseDTO>> getHistoryByUserId(@PathVariable Long userId) {
        List<HistoryResponseDTO> historyRecords = historyService.getHistoryByUserId(userId);
        return new ResponseEntity<>(historyRecords, HttpStatus.OK);
    }

    @GetMapping("/target")
    public ResponseEntity<List<HistoryResponseDTO>> getHistoryByTarget(
            @RequestParam String targetType,
            @RequestParam(required = false) Long targetId) { // targetId có thể không bắt buộc nếu chỉ muốn theo type
        List<HistoryResponseDTO> historyRecords = historyService.getHistoryByTarget(targetType, targetId);
        return new ResponseEntity<>(historyRecords, HttpStatus.OK);
    }

    // Các phương thức tạo/cập nhật History thường không được expose qua API
    // mà được gọi nội bộ từ các Service khác (ví dụ: AppUserService, InvoiceService, etc.).
}