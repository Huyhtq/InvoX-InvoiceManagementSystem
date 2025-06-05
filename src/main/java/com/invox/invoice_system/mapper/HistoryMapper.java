package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.History;
import com.invox.invoice_system.dto.HistoryResponseDTO;

import org.springframework.stereotype.Component;

@Component
public class HistoryMapper {

    // Convert History Entity to HistoryResponseDTO
    public HistoryResponseDTO toResponseDto(History history) {
        if (history == null) {
            return null;
        }

        HistoryResponseDTO dto = new HistoryResponseDTO();
        dto.setId(history.getId());
        dto.setAction(history.getAction()); // Enum HistoryAction
        dto.setTargetType(history.getTargetType());
        dto.setTargetId(history.getTargetId());
        dto.setTimestamp(history.getTimestamp());
        dto.setDetailJson(history.getDetailJson());

        // Chỉ map ID của User, không cần DTO đầy đủ
        if (history.getUser() != null) {
            dto.setUserId(history.getUser().getId());
        } else {
            dto.setUserId(null); // Đảm bảo ID là null nếu user null
        }

        return dto;
    }
}