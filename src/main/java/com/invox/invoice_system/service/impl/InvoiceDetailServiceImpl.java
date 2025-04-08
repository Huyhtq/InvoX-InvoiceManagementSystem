package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailResponseDTO;
import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.InvoiceDetail;
import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.repository.InvoiceDetailRepository;
import com.invox.invoice_system.repository.InvoiceRepository;
import com.invox.invoice_system.repository.ProductRepository;
import com.invox.invoice_system.service.InvoiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceDetailServiceImpl implements InvoiceDetailService {

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public InvoiceDetailResponseDTO createInvoiceDetail(InvoiceDetailRequestDTO dto) {
        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId()).orElseThrow();
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();

        InvoiceDetail detail = InvoiceDetail.builder()
                .id(dto.getId())
                .invoice(invoice)
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();

        return mapToDTO(invoiceDetailRepository.save(detail));
    }

    @Override
    public List<InvoiceDetailResponseDTO> getAllInvoiceDetails() {
        return invoiceDetailRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public InvoiceDetailResponseDTO getInvoiceDetailById(Long id) {
        return invoiceDetailRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public void deleteInvoiceDetail(Long id) {
        invoiceDetailRepository.deleteById(id);
    }

    private InvoiceDetailResponseDTO mapToDTO(InvoiceDetail detail) {
        return new InvoiceDetailResponseDTO(
                detail.getId(),
                detail.getInvoice().getId(),
                detail.getProduct().getId(),
                detail.getQuantity(),
                detail.getUnitPrice()
        );
    }
}
