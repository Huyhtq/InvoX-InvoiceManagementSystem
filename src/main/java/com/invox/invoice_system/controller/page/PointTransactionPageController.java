package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.service.PointTransactionService;
import com.invox.invoice_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections; 
import java.util.List;

@Controller
@RequestMapping("/point-transactions")
@RequiredArgsConstructor
public class PointTransactionPageController {

    private final PointTransactionService pointTransactionService;
    private final CustomerService customerService; // Cần để hiển thị danh sách khách hàng để lọc

    @GetMapping
    public String listPointTransactions(Model model, @RequestParam(required = false) Long customerId) {
        List<PointTransactionResponseDTO> transactions;
        CustomerResponseDTO customer = null;

        if (customerId != null) {
            customer = customerService.getCustomerById(customerId).orElse(null);
            if (customer != null) {
                transactions = pointTransactionService.getPointTransactionsByCustomerId(customerId);
            } else {
                transactions = Collections.emptyList(); // Không tìm thấy khách hàng
                model.addAttribute("errorMessage", "Không tìm thấy khách hàng với ID: " + customerId);
            }
        } else {
            // Nếu không có customerId, có thể hiển thị tất cả (nếu số lượng không quá lớn)
            // Hoặc chuyển hướng về trang chọn khách hàng/báo lỗi
            transactions = Collections.emptyList();
            model.addAttribute("infoMessage", "Vui lòng chọn khách hàng để xem lịch sử điểm.");
        }

        model.addAttribute("transactions", transactions);
        model.addAttribute("customers", customerService.getAllCustomers()); // Để có dropdown chọn khách hàng
        model.addAttribute("selectedCustomer", customer); // Để hiển thị khách hàng đang xem
        
        return "pointtransactions/list-pointtransactions"; // Trả về tên view
    }
}