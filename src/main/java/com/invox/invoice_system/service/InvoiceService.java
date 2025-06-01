package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.enums.InvoiceStatus;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<InvoiceResponseDTO> getAllInvoices();
    Optional<InvoiceResponseDTO> getInvoiceById(Long id);
    InvoiceResponseDTO createInvoice(InvoiceCreationRequestDTO invoiceCreationRequestDTO, Long employeeId);
    InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus newStatus);
    void deleteInvoice(Long id);
}