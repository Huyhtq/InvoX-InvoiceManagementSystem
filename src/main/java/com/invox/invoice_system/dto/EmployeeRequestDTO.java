package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String position;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private EmployeeStatus status;
}