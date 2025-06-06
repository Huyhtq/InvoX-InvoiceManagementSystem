package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.History;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>{
    List<History> findByUserId(Long userId);
    List<History> findByTargetTypeAndTargetId(String targetType, Long targetId);
}
