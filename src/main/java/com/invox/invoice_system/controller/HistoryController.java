package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.HistoryRequestDTO;
import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @PostMapping
    public ResponseEntity<HistoryResponseDTO> createHistory(@RequestBody HistoryRequestDTO dto) {
        return ResponseEntity.ok(historyService.createHistory(dto));
    }

    @GetMapping
    public ResponseEntity<List<HistoryResponseDTO>> getAllHistories() {
        return ResponseEntity.ok(historyService.getAllHistories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoryResponseDTO> getHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getHistoryById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }

// Lấy lịch sử trong khoảng thời gian
    @GetMapping("/between")
    public ResponseEntity<List<HistoryResponseDTO>> getHistoriesBetween(
        @RequestParam("from")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam("to")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
    return ResponseEntity.ok(historyService.getHistoriesBetween(
            Timestamp.valueOf(from), Timestamp.valueOf(to)
    ));
    }

// Lấy lịch sử sau một thời điểm
    @GetMapping("/after")
    public ResponseEntity<List<HistoryResponseDTO>> getHistoriesAfter(
        @RequestParam("from")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from
    ) {
    return ResponseEntity.ok(historyService.getHistoriesAfter(
            Timestamp.valueOf(from)
    ));
    }

// Lấy lịch sử trước một thời điểm
    @GetMapping("/before")
    public ResponseEntity<List<HistoryResponseDTO>> getHistoriesBefore(
        @RequestParam("to")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
    return ResponseEntity.ok(historyService.getHistoriesBefore(
            Timestamp.valueOf(to)
    ));
    }
}
