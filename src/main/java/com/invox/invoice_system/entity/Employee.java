package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    private Long id;

    private String name;
    private String position;
}
