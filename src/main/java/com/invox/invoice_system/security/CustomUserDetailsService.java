package com.invox.invoice_system.security;

import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword()) // đã mã hóa bằng BCrypt
            .roles(mapAccessLevelToRole(user.getAccessLevel()))
            .build();
    }

    private String mapAccessLevelToRole(Integer level) {
        return switch (level) {
            case 1 -> "EMPLOYEE";
            case 2 -> "ACCOUNTANT";
            case 3 -> "MANAGER";
            case 4 -> "OWNER";
            default -> "GUEST";
        };
    }
}
