package com.invox.invoice_system.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthPageController {

    @GetMapping("/login") // Hiển thị trang đăng nhập
    public String login() {
        return "login"; // Trả về file login.html
    }

    @GetMapping("/dashboard") // Hiển thị trang dashboard sau khi đăng nhập
    public String dashboard() {
        return "dashboard"; // Trả về file dashboard.html
    }

    // Nếu bạn muốn URL gốc "/" cũng dẫn đến dashboard sau khi đăng nhập,
    // bạn có thể thêm:
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}