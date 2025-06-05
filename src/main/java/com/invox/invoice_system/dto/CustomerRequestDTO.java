package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private Gender gender;
    // totalPoints, availablePoints, memberRankId sẽ được backend quản lý/gán tự động
}