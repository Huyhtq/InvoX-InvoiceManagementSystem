package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.entity.History;
import com.invox.invoice_system.enums.HistoryAction;
import com.invox.invoice_system.mapper.HistoryMapper;
import com.invox.invoice_system.repository.HistoryRepository;
import com.invox.invoice_system.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HistoryResponseDTO> getAllHistoryRecords() {
        return historyRepository.findAll().stream()
                .map(historyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryResponseDTO> getHistoryByUserId(Long userId) {
        return historyRepository.findByUserId(userId).stream() // Cần phương thức findByUserId trong HistoryRepository
                .map(historyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryResponseDTO> getHistoryByTarget(String targetType, Long targetId) {
        return historyRepository.findByTargetTypeAndTargetId(targetType, targetId).stream() // Cần phương thức này
                .map(historyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    // Sử dụng Propagation.REQUIRES_NEW để đảm bảo hành động được ghi lại
    // ngay cả khi giao dịch chính bị lỗi và rollback.
    // Hoặc nếu không cần mức độ đảm bảo này, có thể chỉ để @Transactional mặc định.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(AppUser user, HistoryAction action, String targetType, Long targetId, String detailJson) {
        History history = new History();
        history.setUser(user);
        history.setAction(action);
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        history.setDetailJson(detailJson);
        // timestamp sẽ tự động được tạo bởi @CreationTimestamp

        historyRepository.save(history);
    }
}