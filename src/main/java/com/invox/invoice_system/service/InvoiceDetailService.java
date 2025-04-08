package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailResponseDTO;

import java.util.List;

public interface InvoiceDetailService {
    InvoiceDetailResponseDTO createInvoiceDetail(InvoiceDetailRequestDTO dto);
    List<InvoiceDetailResponseDTO> getAllInvoiceDetails();
    InvoiceDetailResponseDTO getInvoiceDetailById(Long id);
    void deleteInvoiceDetail(Long id);
}
