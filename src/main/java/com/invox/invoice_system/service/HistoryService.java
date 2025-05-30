package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.enums.HistoryAction;

import java.util.List;

public interface HistoryService {
    List<HistoryResponseDTO> getAllHistoryRecords();
    List<HistoryResponseDTO> getHistoryByUserId(Long userId);
    List<HistoryResponseDTO> getHistoryByTarget(String targetType, Long targetId);
    // Phương thức nội bộ, không phải public API
    void logAction(AppUser user, HistoryAction action, String targetType, Long targetId, String detailJson);
}