package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.entity.Employee; // Import for mapping ID
import com.invox.invoice_system.entity.Role;     // Import for mapping ID
import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class, RoleMapper.class})
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(source = "employee", target = "employee")
    @Mapping(source = "role", target = "role")
    UserResponseDTO toResponseDto(AppUser appUser);

    // Mapping for user registration
    @Mapping(source = "employeeId", target = "employee.id")
    @Mapping(source = "roleId", target = "role.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be set after hashing
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "histories", ignore = true)
    AppUser toEntity(UserRegistrationDTO userRegistrationDTO);

    // Helper to map Employee ID
    default Employee toEmployeeEntity(Long employeeId) {
        if (employeeId == null) return null;
        Employee employee = new Employee();
        employee.setId(employeeId);
        return employee;
    }

    // Helper to map Role ID
    default Role toRoleEntity(Long roleId) {
        if (roleId == null) return null;
        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}