package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Role;
import com.invox.invoice_system.dto.RoleDTO;
import org.springframework.stereotype.Component; // Quan trọng để Spring quản lý

@Component // Đánh dấu là Spring Component để có thể @Autowired
public class RoleMapper {

    // Convert Role Entity to RoleDTO
    public RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    // Convert RoleDTO to Role Entity
    public Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        Role entity = new Role();
        entity.setId(roleDTO.getId()); // ID có thể null khi tạo mới
        entity.setName(roleDTO.getName());
        // appUsers collection sẽ được quản lý bởi JPA
        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Entity hiện có
    public void updateEntityFromDto(RoleDTO dto, Role entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
    }
}