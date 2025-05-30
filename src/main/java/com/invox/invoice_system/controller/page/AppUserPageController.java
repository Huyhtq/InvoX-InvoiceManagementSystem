package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;
import com.invox.invoice_system.service.AppUserService;
import com.invox.invoice_system.service.EmployeeService;
import com.invox.invoice_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users") // Base URL cho các trang người dùng
@RequiredArgsConstructor
public class AppUserPageController {

    private final AppUserService appUserService;
    private final EmployeeService employeeService; // Cần để lấy danh sách nhân viên cho form đăng ký
    private final RoleService roleService;         // Cần để lấy danh sách vai trò cho form đăng ký

    @GetMapping
    public String listUsers(Model model) {
        List<UserResponseDTO> users = appUserService.getAllAppUsers();
        model.addAttribute("users", users);
        return "users/list-users";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        model.addAttribute("employees", employeeService.getAllEmployees()); // Danh sách nhân viên để liên kết
        model.addAttribute("roles", roleService.getAllRoles()); // Danh sách vai trò để gán
        return "users/register-user";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegistrationDTO userRegistrationDTO,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            appUserService.registerNewUser(userRegistrationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản người dùng đã được đăng ký thành công!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userRegistrationDTO);
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("roles", roleService.getAllRoles());
            return "users/register-user";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<UserResponseDTO> userOptional = appUserService.getAppUserById(id);
        if (userOptional.isPresent()) {
            UserResponseDTO userResponseDTO = userOptional.get();
            // Map ResponseDTO to Request/Registration DTO for form pre-population
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
            userRegistrationDTO.setUsername(userResponseDTO.getUsername());
            userRegistrationDTO.setEmployeeId(userResponseDTO.getEmployee() != null ? userResponseDTO.getEmployee().getId() : null);
            userRegistrationDTO.setRoleId(userResponseDTO.getRole() != null ? userResponseDTO.getRole().getId() : null);

            model.addAttribute("user", userRegistrationDTO);
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("roles", roleService.getAllRoles());
            return "users/edit-user";
        }
        return "redirect:/users";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserRegistrationDTO userRegistrationDTO,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            appUserService.updateAppUser(id, userRegistrationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản người dùng đã được cập nhật thành công!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userRegistrationDTO);
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("roles", roleService.getAllRoles());
            return "users/edit-user";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appUserService.deleteAppUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản người dùng đã được xóa thành công!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users";
        }
    }

    // You might need a separate form/page for changing password
    // @GetMapping("/change-password/{id}")
    // @PostMapping("/change-password/{id}")
}