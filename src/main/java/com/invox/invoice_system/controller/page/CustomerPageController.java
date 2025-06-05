package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.enums.Gender;
import com.invox.invoice_system.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerPageController {

    private final CustomerService customerService; 
    
    @GetMapping
    public String listCustomers(Model model) {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers/list-customers";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new CustomerRequestDTO());
        model.addAttribute("genders", Gender.values());
        return "customers/create-customer";
    }

    @PostMapping("/new")
    public String createCustomer(@Valid @ModelAttribute("customer") CustomerRequestDTO customerRequestDTO,
                                 BindingResult bindingResult, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values()); 
            return "customers/create-customer"; 
        }
        try {
            customerService.createCustomer(customerRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được tạo thành công!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) { 
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genders", Gender.values());
            // model.addAttribute("customer", customerRequestDTO); // @ModelAttribute đã làm
            return "customers/create-customer";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<CustomerResponseDTO> customerOptional = customerService.getCustomerById(id);
        if (customerOptional.isPresent()) {
            CustomerResponseDTO customerResponseDTO = customerOptional.get();
            // Chuyển từ ResponseDTO sang RequestDTO để đổ vào form
            CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();

            customerRequestDTO.setId(id); 
            customerRequestDTO.setName(customerResponseDTO.getName());
            customerRequestDTO.setPhone(customerResponseDTO.getPhone());
            customerRequestDTO.setEmail(customerResponseDTO.getEmail());
            customerRequestDTO.setAddress(customerResponseDTO.getAddress());
            customerRequestDTO.setBirthDate(customerResponseDTO.getBirthDate());
            customerRequestDTO.setGender(customerResponseDTO.getGender());

            model.addAttribute("customer", customerRequestDTO);
            model.addAttribute("genders", Gender.values());
            return "customers/create-customer"; // Template form tạo/sửa
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng với ID: " + id);
        return "redirect:/customers";
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Long id,
                                 @Valid @ModelAttribute("customer") CustomerRequestDTO customerRequestDTO,
                                 BindingResult bindingResult, // Nhận kết quả validation
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values()); 
            return "customers/create-customer"; // Trở lại form với lỗi
        }
        try {
            customerService.updateCustomer(id, customerRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được cập nhật thành công!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) { // Hoặc các Exception cụ thể khác từ service
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genders", Gender.values());
            // model.addAttribute("customer", customerRequestDTO); // @ModelAttribute đã làm
            // customerRequestDTO.setId(id); // Quan trọng để form biết là đang edit khi có lỗi
            return "customers/create-customer";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được xóa thành công!");
        } catch (IllegalArgumentException e) { // Hoặc các Exception cụ thể khác
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }
}