package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "History")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    private String action;
    private String targetType;
    private Long targetId;

    private Timestamp timestamp;

    // @Lob
    // private String detailJson;
}
