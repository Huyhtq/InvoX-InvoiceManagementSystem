package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.HistoryAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDTO {
    private Long id;
    private Long userId; // Chỉ cần ID người dùng, không cần DTO đầy đủ
    private HistoryAction action;
    private String targetType;
    private Long targetId;
    private LocalDateTime timestamp;
    private String detailJson;
}