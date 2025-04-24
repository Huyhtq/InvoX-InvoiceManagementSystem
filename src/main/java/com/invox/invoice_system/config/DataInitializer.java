package com.invox.invoice_system.config; // Hoặc com.invox.invoice_system nếu không dùng config

import com.invox.invoice_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private UserService userService;

    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            if (!userService.appUserRepository.findByUsername("admin").isPresent()) {
                userService.createUser("admin", "abc1234", 4);
                System.out.println("Người dùng admin đã được tạo.");
            } else {
                System.out.println("Người dùng admin đã tồn tại.");
            }
        };
    }
}