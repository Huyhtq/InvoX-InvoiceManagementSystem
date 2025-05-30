package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.entity.*;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.enums.PointTransactionType;
import com.invox.invoice_system.mapper.InvoiceDetailMapper;
import com.invox.invoice_system.mapper.InvoiceMapper;
import com.invox.invoice_system.mapper.ProductMapper;
import com.invox.invoice_system.repository.CustomerRepository;
import com.invox.invoice_system.repository.EmployeeRepository;
import com.invox.invoice_system.repository.InvoiceRepository;
import com.invox.invoice_system.service.CustomerService;
import com.invox.invoice_system.service.InvoiceService;
import com.invox.invoice_system.service.PointTransactionService;
import com.invox.invoice_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductService productService; 
    private final CustomerService customerService; 
    private final PointTransactionService pointTransactionService; 
    private final ProductMapper productMapper;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceDetailMapper invoiceDetailMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceResponseDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponseDto);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO createInvoice(InvoiceCreationRequestDTO requestDTO, Long employeeId) {
        // 1. Kiểm tra tồn tại Employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại với ID: " + employeeId));

        // 2. Kiểm tra tồn tại Customer (nếu có)
        Customer customer = null;
        if (requestDTO.getCustomerId() != null) {
            customer = customerRepository.findById(requestDTO.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại với ID: " + requestDTO.getCustomerId()));
        }

        Invoice invoice = invoiceMapper.toEntity(requestDTO);
        invoice.setEmployee(employee);
        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.COMPLETED); // Mặc định hóa đơn hoàn thành

        double totalAmount = 0.0;
        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        // 3. Xử lý từng mặt hàng trong hóa đơn
        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn không được để trống sản phẩm.");
        }

        for (InvoiceDetailRequestDTO item : requestDTO.getItems()) {
            // Lấy ProductResponseDTO từ ProductService
            ProductResponseDTO productResponseDTO = productService.getProductById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + item.getProductId()));

            // Ánh xạ ProductResponseDTO sang Product Entity
            Product product = productMapper.toEntity(productResponseDTO);
            
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho. Chỉ còn: " + product.getQuantity());
            }

            // Tạo InvoiceDetail entity
            InvoiceDetail invoiceDetail = invoiceDetailMapper.toEntity(item);
            invoiceDetail.setInvoice(invoice); // Gán hóa đơn cho chi tiết
            invoiceDetail.setProduct(product);
            invoiceDetail.setUnitPrice(product.getPrice()); // Lấy giá hiện tại của sản phẩm
            invoiceDetail.setProductNameSnapshot(product.getName()); // Lưu tên sản phẩm tại thời điểm bán
            invoiceDetail.setSubTotal(item.getQuantity() * product.getPrice());

            totalAmount += invoiceDetail.getSubTotal();
            invoiceDetails.add(invoiceDetail);

            // Cập nhật số lượng tồn kho của sản phẩm (giảm đi)
            productService.updateProductQuantity(product.getId(), -item.getQuantity());
        }

        invoice.setInvoiceDetails(invoiceDetails);
        invoice.setTotalAmount(totalAmount);
        invoice.setDiscountAmount(0.0); // Mặc định không có giảm giá ban đầu, có thể thêm logic sau
        invoice.setPointsRedeemed(0L);  // Mặc định không dùng điểm ban đầu, có thể thêm logic sau

        // 4. Xử lý giảm giá và đổi điểm (nếu có trong requestDTO)
        // Ví dụ: requestDTO.getDiscountPercentage() hoặc requestDTO.getPointsToRedeem()
        // Tại đây, bạn sẽ áp dụng logic giảm giá và trừ điểm từ customerService
        // Cập nhật invoice.setDiscountAmount(), invoice.setPointsRedeemed()
        // và gọi customerService.redeemPointsFromCustomer(customerId, pointsToRedeem);
        // Sau đó tính toán lại finalAmount

        // Tính toán Final Amount
        double finalAmount = invoice.getTotalAmount() - invoice.getDiscountAmount();
        // Nếu có đổi điểm, trừ tiếp từ finalAmount (ví dụ: 1000 điểm = 1000 VNĐ)
        // finalAmount -= invoice.getPointsRedeemed();
        if (finalAmount < 0) {
            finalAmount = 0.0; // Đảm bảo finalAmount không âm
        }
        invoice.setFinalAmount(finalAmount);

        // 5. Lưu hóa đơn vào DB
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 6. Xử lý tích điểm cho khách hàng (nếu có khách hàng và logic tích điểm)
        if (customer != null) {
            // Giả sử logic tính điểm được thực hiện trong CustomerService
            // customerService.addPointsToCustomer(customer.getId(), điểm_tích_lũy);
            // Để đơn giản, ta sẽ chỉ tích điểm sau khi đã xử lý xong hóa đơn.
            // Số điểm tích được tính dựa trên final_amount và earning_rate của MemberRank
            double pointsEarnedValue = savedInvoice.getFinalAmount() * customer.getMemberRank().getPointsEarningRate();
            Long pointsEarned = (long) Math.floor(pointsEarnedValue); // Làm tròn xuống

            if (pointsEarned > 0) {
                customerService.addPointsToCustomer(
                    customer.getId(),
                    pointsEarned
                );
                // Ghi log giao dịch điểm liên quan đến hóa đơn
                pointTransactionService.createPointTransaction(
                    customer,
                    savedInvoice,
                    PointTransactionType.EARN,
                    pointsEarned,
                    "Tích điểm từ hóa đơn #" + savedInvoice.getId()
                );
            }
        }
        return invoiceMapper.toResponseDto(savedInvoice);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + id));

        // Thêm logic kiểm tra chuyển đổi trạng thái hợp lệ (ví dụ: không thể chuyển từ CANCELLED sang COMPLETED)
        invoice.setStatus(newStatus);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDto(updatedInvoice);
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + id);
        }
        // Thêm logic hoàn lại tồn kho, hoàn lại điểm nếu hóa đơn bị xóa/hủy
        invoiceRepository.deleteById(id);
    }
}