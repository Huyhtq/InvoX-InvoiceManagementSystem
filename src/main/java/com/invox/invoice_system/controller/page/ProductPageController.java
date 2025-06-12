package com.invox.invoice_system.controller.page;

import com.invox.invoice_system.dto.CategoryDTO;
import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import com.invox.invoice_system.enums.ProductStatus;
import com.invox.invoice_system.service.ProductService;
import com.invox.invoice_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model,
                               @RequestParam(required = false) String searchTerm,
                               @RequestParam(required = false) ProductStatus status,
                               @RequestParam(required = false) Long categoryId) {
        
        List<ProductResponseDTO> products = productService.searchProducts(searchTerm, status, categoryId);
        List<CategoryDTO> categories = categoryService.getAllCategories();

        model.addAttribute("productList", products); // Đảm bảo biến này được đưa vào Model
        model.addAttribute("categories", categories); // Đảm bảo biến này được đưa vào Model
        model.addAttribute("productStatuses", ProductStatus.values()); // Đảm bảo biến này được đưa vào Model

        // Đảm bảo các tham số tìm kiếm/lọc cũng được đưa vào Model để giữ lại giá trị trên form
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);

        return "products/list-products";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductRequestDTO());
        model.addAttribute("categories", categoryService.getAllCategories()); // Cần danh sách danh mục để chọn
        model.addAttribute("productStatuses", ProductStatus.values()); // Cần các trạng thái sản phẩm
        return "products/create-product";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute("product") ProductRequestDTO productRequestDTO,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.createProduct(productRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Sản phẩm đã được tạo thành công!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", productRequestDTO);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("productStatuses", ProductStatus.values());
            return "products/create-product";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ProductResponseDTO> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()) {
            ProductResponseDTO productResponseDTO = productOptional.get();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO();

            // Gán đầy đủ các giá trị
            productRequestDTO.setId(productResponseDTO.getId());
            productRequestDTO.setName(productResponseDTO.getName());

            // === THÊM DÒNG NÀY VÀO ===
            productRequestDTO.setSku(productResponseDTO.getSku());
            
            productRequestDTO.setPrice(productResponseDTO.getPrice());
            productRequestDTO.setCostPrice(productResponseDTO.getCostPrice());
            productRequestDTO.setQuantity(productResponseDTO.getQuantity());
            productRequestDTO.setBrand(productResponseDTO.getBrand());
            productRequestDTO.setImageUrl(productResponseDTO.getImageUrl());
            productRequestDTO.setDescription(productResponseDTO.getDescription());
            productRequestDTO.setStatus(productResponseDTO.getStatus());
            productRequestDTO.setCategoryId(productResponseDTO.getCategory() != null ? productResponseDTO.getCategory().getId() : null);

            model.addAttribute("product", productRequestDTO);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("productStatuses", ProductStatus.values());
            return "products/create-product";
        }
        return "redirect:/products";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("product") ProductRequestDTO productRequestDTO,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(id, productRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Sản phẩm đã được cập nhật thành công!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("product", productRequestDTO); // Giữ lại dữ liệu đã nhập
            model.addAttribute("categories", categoryService.getAllCategories()); // THÊM LẠI để form có dropdown
            model.addAttribute("productStatuses", ProductStatus.values()); // THÊM LẠI để form có dropdown
            return "products/create-product"; // Trả về lại form khi có lỗi
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sản phẩm đã được xóa thành công!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        }
    }
}