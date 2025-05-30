package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.RoleDTO;
import com.invox.invoice_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolePageController {

    private final RoleService roleService;

    @GetMapping
    public String listRoles(Model model) {
        List<RoleDTO> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        return "roles/list-roles";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new RoleDTO());
        return "roles/create-role";
    }

    @PostMapping("/new")
    public String createRole(@ModelAttribute("role") RoleDTO roleDTO,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            roleService.createRole(roleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Vai trò đã được tạo thành công!");
            return "redirect:/roles";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", roleDTO);
            return "roles/create-role";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<RoleDTO> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "roles/edit-role";
        }
        return "redirect:/roles";
    }

    @PostMapping("/edit/{id}")
    public String updateRole(@PathVariable Long id,
                             @ModelAttribute("role") RoleDTO roleDTO,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            roleService.updateRole(id, roleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Vai trò đã được cập nhật thành công!");
            return "redirect:/roles";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", roleDTO);
            return "roles/edit-role";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vai trò đã được xóa thành công!");
            return "redirect:/roles";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/roles";
        }
    }
}