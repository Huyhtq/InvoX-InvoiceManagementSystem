package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.InvoiceRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto);
    InvoiceResponseDTO getInvoiceById(Long id);
    List<InvoiceResponseDTO> getAllInvoices();
    InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto);
    void deleteInvoice(Long id);
}
