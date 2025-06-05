package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.FinancialReportDataDTO;
import java.time.LocalDate;

public interface ReportService {
    FinancialReportDataDTO generateFinancialReport(LocalDate startDate, LocalDate endDate);
    FinancialReportDataDTO generateFinancialReportByPeriod(String period); // "lastMonth", "last3Months", "thisQuarter", "thisYear"
}