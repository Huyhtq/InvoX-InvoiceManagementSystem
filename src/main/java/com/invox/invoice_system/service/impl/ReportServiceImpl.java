package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.FinancialReportDataDTO;
import com.invox.invoice_system.dto.ProductSalesReportDTO;
import com.invox.invoice_system.entity.Invoice;
import com.invox.invoice_system.entity.InvoiceDetail;
import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.repository.InvoiceDetailRepository;
import com.invox.invoice_system.repository.InvoiceRepository;
import com.invox.invoice_system.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public FinancialReportDataDTO generateFinancialReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        if (startDate.isAfter(endDate)) {
            // Hoặc bạn có thể trả về DTO rỗng với thông báo lỗi tùy theo cách Controller xử lý
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        FinancialReportDataDTO reportData = new FinancialReportDataDTO();
        reportData.setStartDate(startDate);
        reportData.setEndDate(endDate);

        List<Invoice> completedInvoices = invoiceRepository.findAllByStatusAndInvoiceDateBetween(
                InvoiceStatus.COMPLETED, startDateTime, endDateTime);

        if (completedInvoices.isEmpty()) {
            reportData.setTotalRevenue(BigDecimal.ZERO);
            reportData.setTotalCostOfGoods(BigDecimal.ZERO);
            reportData.setGrossProfit(BigDecimal.ZERO);
            reportData.setNumberOfInvoices(0L);
            reportData.setNumberOfProductsSold(0L);
            reportData.setProductSalesDetails(new ArrayList<>());
            reportData.setSumProductRevenue(BigDecimal.ZERO);
            reportData.setSumProductCost(BigDecimal.ZERO);
            reportData.setSumProductProfit(BigDecimal.ZERO);
            return reportData;
        }

        BigDecimal totalOverallRevenue = BigDecimal.ZERO;
        BigDecimal totalOverallCostOfGoods = BigDecimal.ZERO;
        long totalProductsSoldCount = 0L;

        Map<Long, ProductSalesReportDTO> productSalesMap = new HashMap<>();

        List<InvoiceDetail> allDetails = invoiceDetailRepository.findAllByInvoiceIn(completedInvoices);

        for (InvoiceDetail detail : allDetails) {
            Product product = detail.getProduct();
            if (product == null) {
                // Ghi log hoặc xử lý trường hợp product bị null trong InvoiceDetail nếu có thể xảy ra
                System.err.println("CẢNH BÁO: InvoiceDetail ID " + detail.getId() + " có Product là null.");
                continue;
            }

            BigDecimal itemLineRevenue = BigDecimal.valueOf(detail.getUnitPrice())
                                             .multiply(BigDecimal.valueOf(detail.getQuantity()));
            
            BigDecimal itemLineCost = BigDecimal.ZERO;
            if (product.getCostPrice() != null) {
                itemLineCost = BigDecimal.valueOf(product.getCostPrice())
                                    .multiply(BigDecimal.valueOf(detail.getQuantity()));
            }

            totalOverallRevenue = totalOverallRevenue.add(itemLineRevenue);
            totalOverallCostOfGoods = totalOverallCostOfGoods.add(itemLineCost);
            totalProductsSoldCount += detail.getQuantity();

            ProductSalesReportDTO salesData = productSalesMap.computeIfAbsent(product.getId(), productId -> {
                ProductSalesReportDTO newDto = new ProductSalesReportDTO();
                newDto.setProductId(productId);
                newDto.setProductSku(product.getSku());
                newDto.setProductName(detail.getProductNameSnapshot() != null ? detail.getProductNameSnapshot() : product.getName());
                newDto.setQuantitySold(0L);
                newDto.setTotalRevenue(BigDecimal.ZERO);
                newDto.setTotalCost(BigDecimal.ZERO);
                newDto.setTotalProfit(BigDecimal.ZERO);
                return newDto;
            });

            salesData.setQuantitySold(salesData.getQuantitySold() + detail.getQuantity());
            salesData.setTotalRevenue(salesData.getTotalRevenue().add(itemLineRevenue));
            salesData.setTotalCost(salesData.getTotalCost().add(itemLineCost));
        }

        for (ProductSalesReportDTO dto : productSalesMap.values()) {
            dto.setTotalProfit(dto.getTotalRevenue().subtract(dto.getTotalCost()));
        }

        reportData.setTotalRevenue(totalOverallRevenue);
        reportData.setTotalCostOfGoods(totalOverallCostOfGoods);
        reportData.setGrossProfit(totalOverallRevenue.subtract(totalOverallCostOfGoods));
        reportData.setNumberOfInvoices((long) completedInvoices.size());
        reportData.setNumberOfProductsSold(totalProductsSoldCount);
        reportData.setProductSalesDetails(new ArrayList<>(productSalesMap.values()));

        BigDecimal currentSumProductRevenue = BigDecimal.ZERO;
        BigDecimal currentSumProductCost = BigDecimal.ZERO;
        BigDecimal currentSumProductProfit = BigDecimal.ZERO;

        if (reportData.getProductSalesDetails() != null) {
            for (ProductSalesReportDTO psd : reportData.getProductSalesDetails()) {
                if (psd.getTotalRevenue() != null) {
                    currentSumProductRevenue = currentSumProductRevenue.add(psd.getTotalRevenue());
                }
                if (psd.getTotalCost() != null) {
                    currentSumProductCost = currentSumProductCost.add(psd.getTotalCost());
                }
                if (psd.getTotalProfit() != null) {
                    currentSumProductProfit = currentSumProductProfit.add(psd.getTotalProfit());
                }
            }
        }
        reportData.setSumProductRevenue(currentSumProductRevenue);
        reportData.setSumProductCost(currentSumProductCost);
        reportData.setSumProductProfit(currentSumProductProfit);

        return reportData;
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialReportDataDTO generateFinancialReportByPeriod(String period) {
        LocalDate endDate = LocalDate.now(); 
        LocalDate startDate;
        
        String periodToProcess = (period != null && !period.trim().isEmpty()) ? period.toLowerCase() : "thismonth";

        switch (periodToProcess) {
            case "lastmonth":
                YearMonth lastMonth = YearMonth.now().minusMonths(1);
                startDate = lastMonth.atDay(1);
                endDate = lastMonth.atEndOfMonth();
                break;
            case "last3months":
                startDate = LocalDate.now().minusMonths(3).with(TemporalAdjusters.firstDayOfMonth());
                endDate = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "thisquarter":
                YearMonth currentMonthForQuarter = YearMonth.now();
                int currentQuarter = currentMonthForQuarter.get(IsoFields.QUARTER_OF_YEAR);
                startDate = LocalDate.of(currentMonthForQuarter.getYear(), (currentQuarter - 1) * 3 + 1, 1);
                endDate = startDate.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "thisyear":
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
                endDate = LocalDate.now();
                break;
            case "thismonth":
            default: 
                startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                endDate = LocalDate.now(); 
        }
        return generateFinancialReport(startDate, endDate);
    }
}