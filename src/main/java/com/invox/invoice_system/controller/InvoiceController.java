package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.InvoiceRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@RequestBody InvoiceRequestDTO dto) {
        return ResponseEntity.ok(invoiceService.createInvoice(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> updateInvoice(@PathVariable Long id, @RequestBody InvoiceRequestDTO dto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}
