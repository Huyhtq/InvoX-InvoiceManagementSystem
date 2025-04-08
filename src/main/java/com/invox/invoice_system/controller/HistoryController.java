package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.HistoryRequestDTO;
import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
