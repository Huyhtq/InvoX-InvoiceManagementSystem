package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailResponseDTO;
import com.invox.invoice_system.service.InvoiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-details")
public class InvoiceDetailController {

    @Autowired
    private InvoiceDetailService invoiceDetailService;

    @PostMapping
    public ResponseEntity<InvoiceDetailResponseDTO> createInvoiceDetail(@RequestBody InvoiceDetailRequestDTO dto) {
        return ResponseEntity.ok(invoiceDetailService.createInvoiceDetail(dto));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDetailResponseDTO>> getAll() {
        return ResponseEntity.ok(invoiceDetailService.getAllInvoiceDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceDetailService.getInvoiceDetailById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceDetailService.deleteInvoiceDetail(id);
        return ResponseEntity.noContent().build();
    }
}
