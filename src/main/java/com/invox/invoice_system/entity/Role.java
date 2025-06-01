package com.invox.invoice_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Role", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "role_id_seq_gen") 
    @SequenceGenerator(
        name = "role_id_seq_gen",
        sequenceName = "Role_SEQ",
        allocationSize = 1
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private java.util.List<AppUser> appUsers;
}