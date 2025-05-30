package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.enums.PaymentMethod;
import com.invox.invoice_system.service.InvoiceService;
import com.invox.invoice_system.service.ProductService;
import com.invox.invoice_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoicePageController {

    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final CustomerService customerService;
    
    @GetMapping
    public String listInvoices(Model model) {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        return "invoices/list-invoices";
    }

    @GetMapping("/{id}")
    public String viewInvoiceDetail(@PathVariable Long id, Model model) {
        Optional<InvoiceResponseDTO> invoice = invoiceService.getInvoiceById(id);
        if (invoice.isPresent()) {
            model.addAttribute("invoice", invoice.get());
            return "invoices/detail-invoice";
        }
        return "redirect:/invoices"; // Redirect nếu không tìm thấy
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("invoice", new InvoiceCreationRequestDTO());
        model.addAttribute("products", productService.getAllProducts()); // Danh sách sản phẩm để chọn
        model.addAttribute("customers", customerService.getAllCustomers()); // Danh sách khách hàng để chọn
        model.addAttribute("paymentMethods", PaymentMethod.values()); // Các phương thức thanh toán
        return "invoices/create-invoice";
    }

    @PostMapping("/new")
    public String createInvoice(@ModelAttribute("invoice") InvoiceCreationRequestDTO invoiceCreationRequestDTO,
                                @AuthenticationPrincipal UserDetails currentUser, // Lấy thông tin người dùng đang đăng nhập
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // Lấy employeeId từ UserDetails của người dùng đang đăng nhập
            // Trong UserDetailsService, bạn sẽ cần ánh xạ username thành Employee ID.
            // Hoặc bạn có thể truy vấn AppUser từ username để lấy Employee ID.
            Long employeeId = 1L; // Gán tạm, cần thay bằng ID nhân viên thực tế từ currentUser
            // Ví dụ: Long employeeId = appUserService.findByUsername(currentUser.getUsername()).get().getEmployee().getId();

            InvoiceResponseDTO createdInvoice = invoiceService.createInvoice(invoiceCreationRequestDTO, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Hóa đơn đã được tạo thành công!");
            return "redirect:/invoices/" + createdInvoice.getId(); // Chuyển hướng đến trang chi tiết hóa đơn
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("invoice", invoiceCreationRequestDTO);
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "invoices/create-invoice";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateInvoiceStatus(@PathVariable Long id,
                                      @RequestParam("status") InvoiceStatus newStatus,
                                      RedirectAttributes redirectAttributes) {
        try {
            invoiceService.updateInvoiceStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Trạng thái hóa đơn đã được cập nhật!");
            return "redirect:/invoices/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/invoices/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            invoiceService.deleteInvoice(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hóa đơn đã được xóa thành công!");
            return "redirect:/invoices";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/invoices";
        }
    }
}