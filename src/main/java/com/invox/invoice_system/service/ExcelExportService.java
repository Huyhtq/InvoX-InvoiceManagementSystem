package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.FinancialReportDataDTO;
import com.invox.invoice_system.dto.ProductSalesReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class ExcelExportService {

    public ByteArrayInputStream exportFinancialReportToExcel(FinancialReportDataDTO reportData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet summarySheet = workbook.createSheet("BaoCaoTongHop");
            populateSummarySheet(summarySheet, reportData, workbook); // Giả sử bạn đã có hàm này

            Sheet productDetailSheet = workbook.createSheet("ChiTietSanPhamBanRa");
            populateProductDetailSheet(productDetailSheet, reportData, workbook);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // (Hàm populateSummarySheet và createDataRow giữ nguyên như gợi ý trước)
     private void populateSummarySheet(Sheet sheet, FinancialReportDataDTO reportData, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorders(headerCellStyle);


        String[] headers = {"Chỉ tiêu", "Giá trị"};
        for (int col = 0; col < headers.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headers[col]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowIdx = 1;
        if (reportData != null) {
            createDataRow(sheet, rowIdx++, "Từ ngày", reportData.getStartDate() != null ? reportData.getStartDate().toString() : "N/A", workbook);
            createDataRow(sheet, rowIdx++, "Đến ngày", reportData.getEndDate() != null ? reportData.getEndDate().toString() : "N/A", workbook);
            rowIdx++; 
            createDataRow(sheet, rowIdx++, "Tổng Doanh thu (từ HĐ)", reportData.getTotalRevenue(), workbook);
            createDataRow(sheet, rowIdx++, "Tổng Giá vốn (từ HĐ)", reportData.getTotalCostOfGoods(), workbook);
            createDataRow(sheet, rowIdx++, "Lợi nhuận gộp (từ HĐ)", reportData.getGrossProfit(), workbook);
            createDataRow(sheet, rowIdx++, "Số hóa đơn", reportData.getNumberOfInvoices(), workbook);
            createDataRow(sheet, rowIdx++, "Tổng SP đã bán", reportData.getNumberOfProductsSold(), workbook);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createDataRow(Sheet sheet, int rowIndex, String label, Object value, Workbook workbook) {
        Row row = sheet.createRow(rowIndex);
        CellStyle defaultCellStyle = workbook.createCellStyle();
        applyBorders(defaultCellStyle);

        CellStyle currencyCellStyle = workbook.createCellStyle();
        applyBorders(currencyCellStyle);
        DataFormat currencyFormat = workbook.createDataFormat();
        currencyCellStyle.setDataFormat(currencyFormat.getFormat("#,##0"));


        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(defaultCellStyle);

        Cell valueCell = row.createCell(1);
        if (value instanceof BigDecimal) {
            valueCell.setCellValue(((BigDecimal) value).doubleValue());
            valueCell.setCellStyle(currencyCellStyle);
        } else if (value instanceof Number) {
            valueCell.setCellValue(((Number) value).doubleValue());
             // Nếu là số lượng thì không cần định dạng tiền tệ
            if (!label.contains("Điểm") && !label.contains("Số")) { // Ví dụ kiểm tra nhãn
                 valueCell.setCellStyle(currencyCellStyle);
            } else {
                 valueCell.setCellStyle(defaultCellStyle);
            }
        } else if (value != null) {
            valueCell.setCellValue(value.toString());
            valueCell.setCellStyle(defaultCellStyle);
        } else {
            valueCell.setCellValue("");
            valueCell.setCellStyle(defaultCellStyle);
        }
    }
     private void applyBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }


    private void populateProductDetailSheet(Sheet sheet, FinancialReportDataDTO reportData, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorders(headerCellStyle); // Áp dụng border cho header


        String[] productHeaders = {"STT", "Tên hàng", "SKU", "Tổng số bán ra", "Thu về", "Vốn", "Lợi nhuận"};
        for (int col = 0; col < productHeaders.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(productHeaders[col]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowIdx = 1;
        long stt = 1;
        BigDecimal sumRevenueProducts = BigDecimal.ZERO;
        BigDecimal sumCostProducts = BigDecimal.ZERO;
        BigDecimal sumProfitProducts = BigDecimal.ZERO;

        // Tạo Cell Styles dùng chung cho các ô dữ liệu (bao gồm border)
        CellStyle defaultDataCellStyle = workbook.createCellStyle();
        applyBorders(defaultDataCellStyle);

        CellStyle numericCellStyle = workbook.createCellStyle();
        applyBorders(numericCellStyle);
        numericCellStyle.setAlignment(HorizontalAlignment.RIGHT); // Căn phải cho số

        CellStyle currencyCellStyle = workbook.createCellStyle();
        applyBorders(currencyCellStyle);
        DataFormat currencyDataFormat = workbook.createDataFormat();
        currencyCellStyle.setDataFormat(currencyDataFormat.getFormat("#,##0")); // Định dạng số có dấu phẩy
        currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);


        if (reportData != null && reportData.getProductSalesDetails() != null) {
            for (ProductSalesReportDTO psd : reportData.getProductSalesDetails()) {
                Row productRow = sheet.createRow(rowIdx++);
                
                Cell cellStt = productRow.createCell(0);
                cellStt.setCellValue(stt++);
                cellStt.setCellStyle(defaultDataCellStyle);

                Cell cellName = productRow.createCell(1);
                cellName.setCellValue(psd.getProductName());
                cellName.setCellStyle(defaultDataCellStyle);
                
                Cell cellSku = productRow.createCell(2);
                cellSku.setCellValue(psd.getProductSku());
                cellSku.setCellStyle(defaultDataCellStyle);
                
                Cell cellQty = productRow.createCell(3);
                cellQty.setCellValue(psd.getQuantitySold());
                cellQty.setCellStyle(numericCellStyle); // Dùng style căn phải cho số lượng

                Cell cellRevenue = productRow.createCell(4);
                cellRevenue.setCellValue(psd.getTotalRevenue().doubleValue());
                cellRevenue.setCellStyle(currencyCellStyle);
                sumRevenueProducts = sumRevenueProducts.add(psd.getTotalRevenue());

                Cell cellCost = productRow.createCell(5);
                cellCost.setCellValue(psd.getTotalCost().doubleValue());
                cellCost.setCellStyle(currencyCellStyle);
                sumCostProducts = sumCostProducts.add(psd.getTotalCost());

                Cell cellProfit = productRow.createCell(6);
                cellProfit.setCellValue(psd.getTotalProfit().doubleValue());
                cellProfit.setCellStyle(currencyCellStyle);
                sumProfitProducts = sumProfitProducts.add(psd.getTotalProfit());
            }
        }

        // Thêm dòng Tổng cộng cho chi tiết sản phẩm
        if (reportData != null && reportData.getProductSalesDetails() != null && !reportData.getProductSalesDetails().isEmpty()) {
            Row totalProductRow = sheet.createRow(rowIdx); // Không ++ vì đây là dòng cuối
            CellStyle totalLabelCellStyle = workbook.createCellStyle();
            applyBorders(totalLabelCellStyle);
            totalLabelCellStyle.setFont(headerFont); // Font đậm
            totalLabelCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            Cell totalLabelCell = totalProductRow.createCell(3); // Label "Tổng cộng:" căn phải, thẳng cột "Tổng số bán ra"
            totalLabelCell.setCellValue("Tổng cộng:");
            totalLabelCell.setCellStyle(totalLabelCellStyle);
            
            CellStyle totalValueCellStyle = workbook.createCellStyle();
            applyBorders(totalValueCellStyle);
            totalValueCellStyle.setFont(headerFont);
            totalValueCellStyle.setDataFormat(currencyDataFormat.getFormat("#,##0"));
            totalValueCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            Cell totalRevenueCell = totalProductRow.createCell(4);
            totalRevenueCell.setCellValue(sumRevenueProducts.doubleValue());
            totalRevenueCell.setCellStyle(totalValueCellStyle);

            Cell totalCostCell = totalProductRow.createCell(5);
            totalCostCell.setCellValue(sumCostProducts.doubleValue());
            totalCostCell.setCellStyle(totalValueCellStyle);

            Cell totalProfitCell = totalProductRow.createCell(6);
            totalProfitCell.setCellValue(sumProfitProducts.doubleValue());
            totalProfitCell.setCellStyle(totalValueCellStyle);
        }

        for(int i = 0; i < productHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}