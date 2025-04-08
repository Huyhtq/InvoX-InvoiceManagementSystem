package com.invox.invoice_system.dto;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private Long customerId;
    private Long employeeId;
    private Date createdDate;
    private Long total;
}
