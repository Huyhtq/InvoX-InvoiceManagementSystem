package com.invox.invoice_system.controller.api;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.service.InvoiceService;
import com.invox.invoice_system.service.AppUserService; // Cần để lấy employeeId từ người dùng hiện tại
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceApiController {

    private final InvoiceService invoiceService;
    private final AppUserService appUserService; // Để lấy employeeId

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
        return new ResponseEntity<>(invoices, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(invoice -> new ResponseEntity<>(invoice, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@RequestBody InvoiceCreationRequestDTO invoiceCreationRequestDTO,
                                                          @AuthenticationPrincipal UserDetails currentUser) { // Lấy thông tin người dùng đang đăng nhập
        // Trong thực tế, bạn sẽ cần lấy employeeId từ currentUser.
        // Có thể cần một phương thức trong AppUserService để lấy EmployeeId từ username hoặc từ AppUser entity
        Long employeeId = appUserService.findByUsername(currentUser.getUsername())
                            .map(appUser -> appUser.getEmployee().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Không thể tìm thấy nhân viên liên kết với người dùng hiện tại."));

        InvoiceResponseDTO createdInvoice = invoiceService.createInvoice(invoiceCreationRequestDTO, employeeId);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InvoiceResponseDTO> updateInvoiceStatus(@PathVariable Long id,
                                                                  @RequestParam InvoiceStatus newStatus) {
        InvoiceResponseDTO updatedInvoice = invoiceService.updateInvoiceStatus(id, newStatus);
        return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}