package com.invox.invoice_system.dto;

import com.invox.invoice_system.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private Gender gender;
    private Long totalPoints;
    private Long availablePoints;
    private MemberRankDTO memberRank; // Trả về MemberRank dưới dạng DTO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}