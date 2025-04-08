package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.HistoryRequestDTO;
import com.invox.invoice_system.dto.HistoryResponseDTO;

import java.util.List;

public interface HistoryService {
    HistoryResponseDTO createHistory(HistoryRequestDTO dto);
    List<HistoryResponseDTO> getAllHistories();
    HistoryResponseDTO getHistoryById(Long id);
    void deleteHistory(Long id);
}
