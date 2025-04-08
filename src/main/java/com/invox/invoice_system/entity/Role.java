package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    private Integer id;

    private String name;
}
