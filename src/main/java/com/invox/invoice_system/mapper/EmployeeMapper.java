package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;
import org.springframework.stereotype.Component; 

@Component // Đánh dấu là Spring Component để có thể @Autowired
public class EmployeeMapper {

    // Convert Employee Entity to EmployeeResponseDTO
    public EmployeeResponseDTO toResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setPhone(employee.getPhone());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setPosition(employee.getPosition());
        dto.setHireDate(employee.getHireDate());
        dto.setTerminationDate(employee.getTerminationDate());
        dto.setStatus(employee.getStatus()); // Enum EmployeeStatus
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }

    // Convert EmployeeRequestDTO to Employee Entity (khi tạo mới)
    public Employee toEntity(EmployeeRequestDTO employeeRequestDTO) {
        if (employeeRequestDTO == null) {
            return null;
        }

        Employee entity = new Employee();
        // ID, createdAt, updatedAt, appUser, invoices sẽ được quản lý bởi Service hoặc JPA
        entity.setId(employeeRequestDTO.getId());
        entity.setName(employeeRequestDTO.getName());
        entity.setPhone(employeeRequestDTO.getPhone());
        entity.setEmail(employeeRequestDTO.getEmail());
        entity.setAddress(employeeRequestDTO.getAddress());
        entity.setPosition(employeeRequestDTO.getPosition());
        entity.setHireDate(employeeRequestDTO.getHireDate());
        entity.setTerminationDate(employeeRequestDTO.getTerminationDate());
        entity.setStatus(employeeRequestDTO.getStatus()); // Enum EmployeeStatus

        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Employee hiện có từ DTO
    // Dùng @MappingTarget của MapStruct trước đây, giờ tự code
    public void updateEntityFromDto(EmployeeRequestDTO dto, Employee entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setPosition(dto.getPosition());
        entity.setHireDate(dto.getHireDate());
        entity.setTerminationDate(dto.getTerminationDate());
        entity.setStatus(dto.getStatus());
        // created_at không cập nhật, updated_at tự động bởi @UpdateTimestamp
        // appUser và invoices collection không cập nhật ở đây
    }
}