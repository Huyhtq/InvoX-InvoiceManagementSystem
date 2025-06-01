package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import com.invox.invoice_system.enums.Gender;
import com.invox.invoice_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        model.addAttribute("genders", Gender.values()); // Cần các giá trị giới tính
        return "customers/create-customer";
    }

    @PostMapping("/new")
    public String createCustomer(@ModelAttribute("customer") CustomerRequestDTO customerRequestDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            customerService.createCustomer(customerRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được tạo thành công!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customerRequestDTO);
            model.addAttribute("genders", Gender.values());
            return "customers/create-customer";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<CustomerResponseDTO> customerOptional = customerService.getCustomerById(id);
        if (customerOptional.isPresent()) {
            CustomerResponseDTO customerResponseDTO = customerOptional.get();
            // Chuyển từ ResponseDTO sang RequestDTO để đổ vào form
            CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
            customerRequestDTO.setName(customerResponseDTO.getName());
            customerRequestDTO.setPhone(customerResponseDTO.getPhone());
            customerRequestDTO.setEmail(customerResponseDTO.getEmail());
            customerRequestDTO.setAddress(customerResponseDTO.getAddress());
            customerRequestDTO.setBirthDate(customerResponseDTO.getBirthDate());
            customerRequestDTO.setGender(customerResponseDTO.getGender());

            model.addAttribute("customer", customerRequestDTO);
            model.addAttribute("genders", Gender.values());
            return "customers/create-customer";
        }
        return "redirect:/customers";
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Long id,
                                 @ModelAttribute("customer") CustomerRequestDTO customerRequestDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomer(id, customerRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được cập nhật thành công!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customerRequestDTO);
            model.addAttribute("genders", Gender.values());
            return "customers/create-customer";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khách hàng đã được xóa thành công!");
            return "redirect:/customers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }
}