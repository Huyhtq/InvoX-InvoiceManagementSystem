package com.invox.invoice_system.dto;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDTO {
    private Long id;
    private Long userId;
    private String action;
    private String targetType;
    private Long targetId;
    private Timestamp timestamp;
}
