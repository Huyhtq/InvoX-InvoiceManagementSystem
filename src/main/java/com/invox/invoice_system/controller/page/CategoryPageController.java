package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.CategoryDTO;
import com.invox.invoice_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller; // Dùng @Controller cho Views
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories") // Base URL cho các trang HTML
@RequiredArgsConstructor
public class CategoryPageController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "categories/list-categories"; // Trả về tên view Thymeleaf
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "categories/create-category";
    }

    @PostMapping("/new")
    public String createCategory(@ModelAttribute("category") CategoryDTO categoryDTO, Model model) {
        try {
            categoryService.createCategory(categoryDTO);
            return "redirect:/categories"; // Redirect sau POST để tránh gửi lại form
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", categoryDTO); // Giữ lại dữ liệu đã nhập
            return "categories/create-category";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<CategoryDTO> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categories/create-category";
        }
        return "redirect:/categories"; // Chuyển hướng nếu không tìm thấy
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute("category") CategoryDTO categoryDTO, Model model) {
        try {
            categoryService.updateCategory(id, categoryDTO);
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("category", categoryDTO);
            return "categories/create-category";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, Model model) {
        try {
            categoryService.deleteCategory(id);
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi và có thể hiển thị thông báo trên trang danh sách
            return "redirect:/categories?error=" + e.getMessage();
        }
    }
}
