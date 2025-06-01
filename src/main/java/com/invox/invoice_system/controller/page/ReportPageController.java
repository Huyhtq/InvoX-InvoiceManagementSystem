package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.FinancialReportDataDTO;
import com.invox.invoice_system.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.invox.invoice_system.service.ExcelExportService;
import com.invox.invoice_system.service.PdfExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportPageController {

    private final ReportService reportService;

    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;


    @GetMapping("/financial/export/excel")
    public ResponseEntity<InputStreamResource> exportFinancialReportToExcel(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        FinancialReportDataDTO reportData = getReportDataForExport(period, startDate, endDate); // Hàm helper lấy data
        ByteArrayInputStream bis = excelExportService.exportFinancialReportToExcel(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=BaoCaoTaiChinh.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Hoặc application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/financial/export/pdf")
    public ResponseEntity<InputStreamResource> exportFinancialReportToPdf(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
        
        FinancialReportDataDTO reportData = getReportDataForExport(period, startDate, endDate);
        ByteArrayInputStream bis = pdfExportService.exportFinancialReportToPdf(reportData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=BaoCaoTaiChinh.pdf"); // inline để mở trong trình duyệt, attachment để tải về

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // Hàm helper để tránh lặp code lấy dữ liệu báo cáo
    private FinancialReportDataDTO getReportDataForExport(String period, LocalDate startDate, LocalDate endDate) {
        if (period != null && !period.isEmpty()) {
            return reportService.generateFinancialReportByPeriod(period);
        } else if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                // Xử lý lỗi ngày không hợp lệ, có thể trả về DTO rỗng hoặc throw exception
                FinancialReportDataDTO emptyData = new FinancialReportDataDTO();
                emptyData.setStartDate(startDate);
                emptyData.setEndDate(endDate);
                // Thêm thông báo lỗi nếu cần để hiển thị hoặc ghi log
                return emptyData;
            }
            return reportService.generateFinancialReport(startDate, endDate);
        }
        // Mặc định hoặc trả về lỗi nếu không có filter hợp lệ
        return reportService.generateFinancialReportByPeriod("thismonth"); 
    }

    @GetMapping("/financial")
    public String showFinancialReportPage(
            @RequestParam(name = "period", required = false) String period, // "lastMonth", "last3Months", "thisQuarter", "thisYear"
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        FinancialReportDataDTO reportData;

        if (period != null && !period.isEmpty()) {
            reportData = reportService.generateFinancialReportByPeriod(period);
        } else if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                model.addAttribute("errorMessage", "Ngày bắt đầu không được sau ngày kết thúc.");
                // Tạo reportData rỗng hoặc với giá trị mặc định
                reportData = new FinancialReportDataDTO(); 
                reportData.setStartDate(startDate);
                reportData.setEndDate(endDate);
            } else {
                reportData = reportService.generateFinancialReport(startDate, endDate);
            }
        } else {
            // Mặc định khi mới vào trang, ví dụ: báo cáo cho tháng này
            reportData = reportService.generateFinancialReportByPeriod("thismonth"); // Hoặc một khoảng thời gian mặc định khác
        }

        model.addAttribute("reportData", reportData);
        model.addAttribute("pageTitle", "Báo cáo Tài chính");
        // Truyền lại các tham số filter để hiển thị trên form
        model.addAttribute("selectedPeriod", period);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);

        return "reports/financial-report"; 
    }
}