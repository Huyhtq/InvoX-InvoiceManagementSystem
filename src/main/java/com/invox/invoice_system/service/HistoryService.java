package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.HistoryRequestDTO;
import com.invox.invoice_system.dto.HistoryResponseDTO;

import java.util.List;
import java.sql.Timestamp;

public interface HistoryService {
    HistoryResponseDTO createHistory(HistoryRequestDTO dto);
    List<HistoryResponseDTO> getAllHistories();
    HistoryResponseDTO getHistoryById(Long id);
    void deleteHistory(Long id);
    List<HistoryResponseDTO> getHistoriesBetween(Timestamp from, Timestamp to);
    List<HistoryResponseDTO> getHistoriesAfter(Timestamp from);
    List<HistoryResponseDTO> getHistoriesBefore(Timestamp to);
}
