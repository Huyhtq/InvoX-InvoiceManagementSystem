package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByTimestampBetween(Timestamp start, Timestamp end);
    List<History> findByTimestampAfter(Timestamp time);
    List<History> findByTimestampBefore(Timestamp time);
}
