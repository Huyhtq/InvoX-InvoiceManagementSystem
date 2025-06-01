package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceDetailRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.entity.*;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.enums.PaymentMethod;
import com.invox.invoice_system.enums.PointTransactionType;
import com.invox.invoice_system.mapper.InvoiceMapper;
import com.invox.invoice_system.mapper.ProductMapper;
import com.invox.invoice_system.repository.CustomerRepository;
import com.invox.invoice_system.repository.EmployeeRepository;
import com.invox.invoice_system.repository.InvoiceRepository;

// import com.invox.invoice_system.repository.InvoiceDetailRepository; 
import com.invox.invoice_system.service.CustomerService;
import com.invox.invoice_system.service.InvoiceService;
import com.invox.invoice_system.service.PointTransactionService;
import com.invox.invoice_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    // private final InvoiceDetailMapper invoiceDetailMapper; // Bạn có dùng mapper này để toEntity không? Nếu có thì giữ lại.

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

        // 3. Kiểm tra items không được rỗng
        if (requestDTO.getItems() == null || requestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất một sản phẩm.");
        }

        // === BẮT ĐẦU XÂY DỰNG INVOICE VÀ INVOICEDETAILS ===
        
        // Tạo đối tượng Invoice mới
        // Nếu bạn có InvoiceMapper.toEntity(InvoiceCreationRequestDTO), hãy dùng nó.
        // Nếu không, khởi tạo thủ công:
        Invoice invoice = new Invoice(); 
        invoice.setEmployee(employee);
        invoice.setCustomer(customer);
        
        String paymentMethodString = requestDTO.getPaymentMethod();
        if (paymentMethodString != null && !paymentMethodString.trim().isEmpty()) {
            try {
                invoice.setPaymentMethod(PaymentMethod.valueOf(paymentMethodString.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + paymentMethodString, e);
            }
        } else {
             throw new IllegalArgumentException("Phương thức thanh toán không được để trống.");
        }
        
        invoice.setNotes(requestDTO.getNotes());
        invoice.setStatus(InvoiceStatus.COMPLETED); // Hoặc trạng thái ban đầu phù hợp

        // Chuẩn bị danh sách InvoiceDetail để gán cho Invoice
        // Nếu Invoice.invoiceDetails là Set, dùng new HashSet<>()
        List<InvoiceDetail> detailsForThisInvoice = new ArrayList<>(); 
        BigDecimal sumOfItemSubtotals = BigDecimal.ZERO;

        for (InvoiceDetailRequestDTO itemDto : requestDTO.getItems()) {
            ProductResponseDTO productResponseDTO = productService.getProductById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + itemDto.getProductId()));
            
            Product productEntity = productMapper.toEntity(productResponseDTO); // Giả sử productMapper.toEntity(ProductResponseDTO) tồn tại

            if (productEntity.getQuantity() < itemDto.getQuantity()) {
                throw new IllegalArgumentException("Sản phẩm '" + productEntity.getName() + "' không đủ số lượng tồn kho. Chỉ còn: " + productEntity.getQuantity());
            }

            InvoiceDetail detail = new InvoiceDetail();
            detail.setProduct(productEntity);
            detail.setQuantity(itemDto.getQuantity().longValue()); // Đảm bảo kiểu Long nếu entity là Long
            detail.setUnitPrice(productEntity.getPrice()); 
            detail.setProductNameSnapshot(productEntity.getName()); 
            
            BigDecimal itemSubtotal = BigDecimal.valueOf(productEntity.getPrice()).multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            detail.setSubTotal(itemSubtotal.longValue());

            // ***** THIẾT LẬP MỐI QUAN HỆ HAI CHIỀU *****
            detail.setInvoice(invoice); // Gán Invoice cha cho InvoiceDetail con
            
            detailsForThisInvoice.add(detail); // Thêm vào list sẽ được gán cho Invoice cha
            sumOfItemSubtotals = sumOfItemSubtotals.add(itemSubtotal);
        }
        
        // Gán danh sách InvoiceDetail (đã được liên kết) cho Invoice cha
        invoice.setInvoiceDetails(detailsForThisInvoice);


        // === TIẾP TỤC TÍNH TOÁN CÁC GIÁ TRỊ CHO INVOICE ===
        BigDecimal invoiceTaxRate = BigDecimal.ZERO;
        if (requestDTO.getInvoiceTaxRatePercentage() != null && requestDTO.getInvoiceTaxRatePercentage() >= 0) {
            invoiceTaxRate = BigDecimal.valueOf(requestDTO.getInvoiceTaxRatePercentage()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        } else {
            invoiceTaxRate = BigDecimal.valueOf(0.10); // Mặc định 10%
        }
        BigDecimal totalInvoiceTax = sumOfItemSubtotals.multiply(invoiceTaxRate).setScale(0, RoundingMode.HALF_UP);

        BigDecimal amountBeforeDiscountsAndPoints = sumOfItemSubtotals.add(totalInvoiceTax);
        BigDecimal invoiceLevelDiscountAmount = BigDecimal.ZERO; // Hiện tại chưa có logic cho discount này từ DTO
        invoice.setDiscountAmount(invoiceLevelDiscountAmount.longValue());
        BigDecimal amountAfterInvoiceDiscount = amountBeforeDiscountsAndPoints.subtract(invoiceLevelDiscountAmount);

        BigDecimal pointsValueToDeduct = BigDecimal.ZERO;
        Long pointsRedeemed = 0L;
        if (customer != null && requestDTO.getPointsToRedeem() != null && requestDTO.getPointsToRedeem() > 0) {
            pointsRedeemed = requestDTO.getPointsToRedeem().longValue();
            if (pointsRedeemed % 1000 != 0) {
                throw new IllegalArgumentException("Số điểm sử dụng phải là bội số của 1000.");
            }
            if (customer.getAvailablePoints() < pointsRedeemed) {
                throw new IllegalArgumentException("Khách hàng không đủ " + pointsRedeemed + " điểm để sử dụng. Chỉ có: " + customer.getAvailablePoints());
            }
            pointsValueToDeduct = BigDecimal.valueOf(pointsRedeemed);
        }
        invoice.setPointsRedeemed(pointsRedeemed);

        BigDecimal finalAmountBigDecimal = amountAfterInvoiceDiscount.subtract(pointsValueToDeduct);
        if (finalAmountBigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            finalAmountBigDecimal = BigDecimal.ZERO;
        }
        invoice.setFinalAmount(finalAmountBigDecimal.longValue());
        invoice.setTotalAmount(amountAfterInvoiceDiscount.longValue()); // Theo định nghĩa schema của bạn

        // 4. LƯU HÓA ĐƠN (và các InvoiceDetail liên kết nhờ CascadeType.ALL)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 5. Cập nhật tồn kho sản phẩm
        // Nên lặp qua savedInvoice.getInvoiceDetails() để chắc chắn là các đối tượng đã được quản lý bởi persistence context
        if (savedInvoice.getInvoiceDetails() != null) {
            for (InvoiceDetail detail : savedInvoice.getInvoiceDetails()) { 
                productService.updateProductQuantity(detail.getProduct().getId(), -detail.getQuantity());
            }
        }

        // 6. Xử lý TRỪ ĐIỂM nếu có
        if (customer != null && pointsRedeemed > 0) {
            customerService.redeemPointsFromCustomer(customer.getId(), pointsRedeemed, savedInvoice);
        }

        // 7. Xử lý TÍCH ĐIỂM cho hóa đơn này
        if (customer != null) {
            BigDecimal baseAmountForEarningPoints = finalAmountBigDecimal;
            BigDecimal earningRateBigDecimal = BigDecimal.ZERO;
            if(customer.getMemberRank() != null && customer.getMemberRank().getPointsEarningRate() != null) {
                earningRateBigDecimal = customer.getMemberRank().getPointsEarningRate();
            }
            
            BigDecimal pointsEarnedValueBigDecimal = baseAmountForEarningPoints.multiply(earningRateBigDecimal);
            Long pointsEarned = pointsEarnedValueBigDecimal.setScale(0, RoundingMode.FLOOR).longValue();

            if (pointsEarned > 0) {
                customerService.updateCustomerPointsAfterEarning(customer.getId(), pointsEarned); 
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

    // ... các phương thức updateInvoiceStatus và deleteInvoice không thay đổi ...
    @Override
    @Transactional
    public InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + id));
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
        // Cần thêm logic hoàn lại tồn kho, hoàn lại điểm nếu cần thiết trước khi xóa
        invoiceRepository.deleteById(id);
    }
}