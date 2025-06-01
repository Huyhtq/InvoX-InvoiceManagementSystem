package com.invox.invoice_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSalesReportDTO {
    private Long productId;
    private String productSku;
    private String productName;
    private Long quantitySold = 0L;         // Tổng số lượng bán
    private BigDecimal totalRevenue = BigDecimal.ZERO; // Tổng doanh thu từ sản phẩm này
    private BigDecimal totalCost = BigDecimal.ZERO;    // Tổng giá vốn của sản phẩm này đã bán
    private BigDecimal totalProfit = BigDecimal.ZERO;  // Lợi nhuận từ sản phẩm này
}