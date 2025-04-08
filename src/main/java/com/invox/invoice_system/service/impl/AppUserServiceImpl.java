package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.AppUserLoginDTO;
import com.invox.invoice_system.dto.AppUserRequestDTO;
import com.invox.invoice_system.dto.AppUserResponseDTO;
import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.entity.Role; 
import com.invox.invoice_system.repository.AppUserRepository;
import com.invox.invoice_system.repository.EmployeeRepository;
import com.invox.invoice_system.repository.RoleRepository;
import com.invox.invoice_system.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public AppUserResponseDTO createUser(AppUserRequestDTO dto) {
        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // TODO: Mã hóa mật khẩu (ví dụ: dùng BCrypt)

        // Gán Employee
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + dto.getEmployeeId()));
            user.setEmployee(employee);
        }

        // Gán Role
        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));
            user.setRole(role);
        }

        AppUser saved = appUserRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    public AppUserResponseDTO getUserById(Long id) {
        return appUserRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public List<AppUserResponseDTO> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        appUserRepository.deleteById(id);
    }

    @Override
    public AppUserResponseDTO updateUser(Long id, AppUserRequestDTO dto) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword()); // TODO: Mã hóa mật khẩu
        }

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + dto.getEmployeeId()));
            user.setEmployee(employee);
        }

        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + dto.getRoleId()));
            user.setRole(role);
        }

        AppUser updated = appUserRepository.save(user);
        return mapToDTO(updated);
    }

    @Override
    public AppUserResponseDTO login(AppUserLoginDTO dto) {
        AppUser user = appUserRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!user.getPassword().equals(dto.getPassword())) { // TODO: So sánh mật khẩu đã mã hóa
            throw new RuntimeException("Invalid username or password");
        }
        return mapToDTO(user);
    }

    private AppUserResponseDTO mapToDTO(AppUser user) {
        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmployeeId(user.getEmployee() != null ? user.getEmployee().getId() : null);
        dto.setRoleId(user.getRole() != null ? user.getRole().getId() : null);
        return dto;
    }
}