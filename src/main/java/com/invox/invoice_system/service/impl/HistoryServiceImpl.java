package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.HistoryRequestDTO;
import com.invox.invoice_system.dto.HistoryResponseDTO;
import com.invox.invoice_system.entity.History;
import com.invox.invoice_system.repository.AppUserRepository;
import com.invox.invoice_system.repository.HistoryRepository;
import com.invox.invoice_system.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public HistoryResponseDTO createHistory(HistoryRequestDTO dto) {
        History history = History.builder()
                .id(dto.getId())
                .user(userRepository.findById(dto.getUserId()).orElseThrow())
                .action(dto.getAction())
                .targetType(dto.getTargetType())
                .targetId(dto.getTargetId())
                .timestamp(dto.getTimestamp())
                .build();

        return mapToDTO(historyRepository.save(history));
    }

    @Override
    public List<HistoryResponseDTO> getAllHistories() {
        return historyRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public HistoryResponseDTO getHistoryById(Long id) {
        return historyRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }

    private HistoryResponseDTO mapToDTO(History history) {
        return new HistoryResponseDTO(
                history.getId(),
                history.getUser().getId(),
                history.getAction(),
                history.getTargetType(),
                history.getTargetId(),
                history.getTimestamp()
        );
    }
}
