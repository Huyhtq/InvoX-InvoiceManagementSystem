package com.invox.invoice_system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class FinancialReportDataDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;        // Tổng doanh thu (tổng tiền hàng Invoice.totalAmount)
    private BigDecimal totalCostOfGoods;    // Tổng giá vốn (từ Product.costPrice của các sản phẩm đã bán)
    private BigDecimal grossProfit;         // Lợi nhuận gộp (totalRevenue - totalCostOfGoods)
    private Long numberOfInvoices;
    private Long numberOfProductsSold;
    private List<ProductSalesReportDTO> productSalesDetails = new ArrayList<>();
    private BigDecimal sumProductRevenue = BigDecimal.ZERO;
    private BigDecimal sumProductCost = BigDecimal.ZERO;
    private BigDecimal sumProductProfit = BigDecimal.ZERO;
}