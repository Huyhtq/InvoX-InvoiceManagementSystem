package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.InvoiceRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.entity.*;
import com.invox.invoice_system.repository.*;
import com.invox.invoice_system.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setId(dto.getId());
        invoice.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow());
        invoice.setEmployee(employeeRepository.findById(dto.getEmployeeId()).orElseThrow());
        invoice.setDate(dto.getCreatedDate());
        invoice.setTotal(dto.getTotal());

        return mapToDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponseDTO getInvoiceById(Long id) {
        return invoiceRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setCustomer(customerRepository.findById(dto.getCustomerId()).orElseThrow());
        invoice.setEmployee(employeeRepository.findById(dto.getEmployeeId()).orElseThrow());
        invoice.setDate(dto.getCreatedDate());
        invoice.setTotal(dto.getTotal());

        return mapToDTO(invoiceRepository.save(invoice));
    }

    @Override
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    private InvoiceResponseDTO mapToDTO(Invoice invoice) {
        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getCustomer().getId(),
                invoice.getEmployee().getId(),
                invoice.getDate(),
                invoice.getTotal()
        );
    }
}
