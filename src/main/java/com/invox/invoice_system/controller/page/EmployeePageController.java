package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;
import com.invox.invoice_system.enums.EmployeeStatus;
import com.invox.invoice_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeePageController {

    private final EmployeeService employeeService;

    @GetMapping
    public String listEmployees(Model model) {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employees/list-employees";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDTO());
        model.addAttribute("employeeStatuses", EmployeeStatus.values()); // Cần các trạng thái nhân viên
        return "employees/create-employee";
    }

    @PostMapping("/new")
    public String createEmployee(@ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.createEmployee(employeeRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được tạo thành công!");
            return "redirect:/employees";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employeeRequestDTO);
            model.addAttribute("employeeStatuses", EmployeeStatus.values());
            return "employees/create-employee";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<EmployeeResponseDTO> employeeOptional = employeeService.getEmployeeById(id);
        if (employeeOptional.isPresent()) {
            EmployeeResponseDTO employeeResponseDTO = employeeOptional.get();
            EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();

            // === DÒNG SỬA QUAN TRỌNG NHẤT ===
            employeeRequestDTO.setId(employeeResponseDTO.getId()); // <-- GÁN ID TẠI ĐÂY

            // Sao chép các thuộc tính khác như cũ
            employeeRequestDTO.setName(employeeResponseDTO.getName());
            employeeRequestDTO.setPhone(employeeResponseDTO.getPhone());
            employeeRequestDTO.setEmail(employeeResponseDTO.getEmail());
            employeeRequestDTO.setAddress(employeeResponseDTO.getAddress());
            employeeRequestDTO.setPosition(employeeResponseDTO.getPosition());
            employeeRequestDTO.setHireDate(employeeResponseDTO.getHireDate());
            employeeRequestDTO.setTerminationDate(employeeResponseDTO.getTerminationDate());
            employeeRequestDTO.setStatus(employeeResponseDTO.getStatus());

            model.addAttribute("employee", employeeRequestDTO);
            model.addAttribute("employeeStatuses", EmployeeStatus.values());
            return "employees/create-employee"; // Tên file template của bạn có thể khác
        }
        return "redirect:/employees";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.updateEmployee(id, employeeRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được cập nhật thành công!");
            return "redirect:/employees";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employeeRequestDTO);
            model.addAttribute("employeeStatuses", EmployeeStatus.values());
            return "employees/create-employee";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id); // Hoặc deactivateEmployee
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được xóa/vô hiệu hóa thành công!");
            return "redirect:/employees";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deactivateEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được vô hiệu hóa thành công!");
            return "redirect:/employees";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employees";
        }
    }
}