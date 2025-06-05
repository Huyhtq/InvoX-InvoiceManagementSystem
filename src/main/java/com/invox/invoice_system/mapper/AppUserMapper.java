package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;        // Cần cho UserResponseDTO
import lombok.RequiredArgsConstructor; // Giữ lại nếu bạn muốn dùng constructor injection
import org.springframework.stereotype.Component; // Quan trọng để Spring quản lý

@Component // Đánh dấu là Spring Component để có thể @Autowired
@RequiredArgsConstructor // Nếu bạn có các dependency khác cần inject vào mapper này
public class AppUserMapper {

    // Cần inject các mapper khác nếu bạn muốn ánh xạ các đối tượng lồng nhau
    private final EmployeeMapper employeeMapper;
    private final RoleMapper roleMapper;

    // Convert AppUser Entity to UserResponseDTO
    public UserResponseDTO toResponseDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(appUser.getId());
        dto.setUsername(appUser.getUsername());

        // Ánh xạ Employee Entity sang EmployeeResponseDTO
        if (appUser.getEmployee() != null) {
            dto.setEmployee(employeeMapper.toResponseDto(appUser.getEmployee())); // Gọi employeeMapper
        }

        // Ánh xạ Role Entity sang RoleDTO
        if (appUser.getRole() != null) {
            dto.setRole(roleMapper.toDto(appUser.getRole())); // Gọi roleMapper
        }

        dto.setCreatedAt(appUser.getCreatedAt());
        dto.setUpdatedAt(appUser.getUpdatedAt());
        return dto;
    }

    // Convert UserRegistrationDTO to AppUser Entity (khi tạo mới)
    public AppUser toEntity(UserRegistrationDTO userRegistrationDTO) {
        if (userRegistrationDTO == null) {
            return null;
        }

        AppUser entity = new AppUser();
        entity.setId(userRegistrationDTO.getId());
        entity.setUsername(userRegistrationDTO.getUsername());

        // Employee và Role sẽ được gán bởi Service sau khi tìm kiếm theo ID
        // Các helper methods toEmployeeEntity và toRoleEntity có thể được bỏ qua ở đây
        // vì Service sẽ tự tìm và set Employee/Role Entity đầy đủ.

        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một AppUser hiện có từ DTO
    public void updateEntityFromDto(UserRegistrationDTO dto, AppUser entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setUsername(dto.getUsername());
    }
}