package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    private Integer id;

    private String name;
}
