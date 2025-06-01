package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.InvoiceCreationRequestDTO;
import com.invox.invoice_system.dto.InvoiceResponseDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.enums.InvoiceStatus;
import com.invox.invoice_system.enums.PaymentMethod;
import com.invox.invoice_system.service.InvoiceService;
import com.invox.invoice_system.service.ProductService;
import com.invox.invoice_system.service.CustomerService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
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
        return "redirect:/invoices";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        InvoiceCreationRequestDTO invoiceDto = new InvoiceCreationRequestDTO();
        invoiceDto.setItems(new ArrayList<>()); 
        model.addAttribute("invoice", invoiceDto);

        List<ProductResponseDTO> productsToSerialize; 
        try {
            productsToSerialize = productService.getAllProducts();
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi productService.getAllProducts(): " + e.getMessage());
            e.printStackTrace();
            productsToSerialize = Collections.emptyList();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String productsJsonString = "[]";
        try {
            productsJsonString = objectMapper.writeValueAsString(productsToSerialize);
        } catch (JsonProcessingException e) {
            System.err.println("NGHIÊM TRỌNG: Lỗi khi serialize products sang JSON trong InvoicePageController: " + e.getMessage());
            e.printStackTrace();
        }
        model.addAttribute("productsJson", productsJsonString);
        
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
            Long employeeId = 1L; // Gán tạm, cần thay bằng ID nhân viên thực tế từ currentUser

            InvoiceResponseDTO createdInvoice = invoiceService.createInvoice(invoiceCreationRequestDTO, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Hóa đơn đã được tạo thành công!");
            return "redirect:/invoices/" + createdInvoice.getId(); // Chuyển hướng đến trang chi tiết hóa đơn
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            if (invoiceCreationRequestDTO.getItems() == null) {
                invoiceCreationRequestDTO.setItems(new ArrayList<>()); // <-- ĐẢM BẢO NÓ KHÔNG NULL KHI CÓ LỖI
            }
            model.addAttribute("invoice", invoiceCreationRequestDTO);

            List<?> productsToSerializeOnError;
            try {
                productsToSerializeOnError = productService.getAllProducts();
            } catch (Exception exService) {
                exService.printStackTrace();
                productsToSerializeOnError = Collections.emptyList();
            }
            ObjectMapper objectMapperOnError = new ObjectMapper();
            objectMapperOnError.registerModule(new JavaTimeModule());
            String productsJsonOnError = "[]";
            try {
                productsJsonOnError = objectMapperOnError.writeValueAsString(productsToSerializeOnError);
            } catch (JsonProcessingException exJson) {
                exJson.printStackTrace();
            }
            model.addAttribute("productsJson", productsJsonOnError);
            
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