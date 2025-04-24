package com.invox.invoice_system.service;

import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    public AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(String username, String rawPassword, Integer accessLevel) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // Mã hóa mật khẩu
        user.setAccessLevel(accessLevel);
        appUserRepository.save(user);
    }
}