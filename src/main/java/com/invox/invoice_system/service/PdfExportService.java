package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.FinancialReportDataDTO;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    private final TemplateEngine templateEngine; // Inject Thymeleaf TemplateEngine

    public PdfExportService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public ByteArrayInputStream exportFinancialReportToPdf(FinancialReportDataDTO reportData) {
        Context context = new Context();
        context.setVariable("reportData", reportData);
        // Thêm các biến khác nếu template PDF cần

        // Render một template Thymeleaf (HTML) được thiết kế riêng cho PDF
        String htmlContent = templateEngine.process("reports/pdf/financial-report-pdf-template", context);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, byteArrayOutputStream);

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}