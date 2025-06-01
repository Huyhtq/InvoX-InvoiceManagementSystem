package com.invox.invoice_system.entity;

import com.invox.invoice_system.enums.HistoryAction; // Import Enum HistoryAction
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "History")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "history_id_seq_gen")
    @SequenceGenerator(
        name = "history_id_seq_gen",
        sequenceName = "History_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private HistoryAction action;

    @Column(name = "target_type", nullable = false, length = 100)
    private String targetType; // Ví dụ: "Product", "Invoice"

    @Column(name = "target_id")
    private Long targetId; // ID của bản ghi bị ảnh hưởng

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "detail_json", columnDefinition = "CLOB") // CLOB để lưu JSON
    private String detailJson; // Chi tiết thay đổi (JSON của đối tượng trước/sau)
}