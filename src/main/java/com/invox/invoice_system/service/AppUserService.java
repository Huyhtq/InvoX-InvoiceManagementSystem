package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;
import com.invox.invoice_system.entity.AppUser; // Có thể cần trả về entity AppUser cho UserDetailsService

import java.util.List;
import java.util.Optional;

public interface AppUserService {
    List<UserResponseDTO> getAllAppUsers();
    Optional<UserResponseDTO> getAppUserById(Long id);
    UserResponseDTO registerNewUser(UserRegistrationDTO userRegistrationDTO);
    UserResponseDTO updateAppUser(Long id, UserRegistrationDTO userRegistrationDTO);
    void deleteAppUser(Long id);
    Optional<AppUser> findByUsername(String username); // Dùng cho Spring Security
    void changePassword(Long userId, String newPassword);
}