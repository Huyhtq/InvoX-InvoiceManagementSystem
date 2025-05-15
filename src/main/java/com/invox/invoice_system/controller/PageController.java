package com.invox.invoice_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/invoices")
    public String showInvoicesPage(Model model) {
        return "invoices"; // Trả về templates/invoices.html
    }

    @GetMapping("/customers")
    public String showCustomersPage() {
        return "customers"; // templates/customers.html
    }

    @GetMapping("/products")
    public String showProductsPage() {
        return "products"; // templates/products.html
    }

    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "dashboard";
    }

    @GetMapping("/reports")
    public String showReportsPage() {
        return "reports";
    }

    @GetMapping("/history")
    public String showHistoryPage() {
        return "history";
    }
}
