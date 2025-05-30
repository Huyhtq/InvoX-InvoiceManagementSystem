package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeResponseDTO toResponseDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "appUser", ignore = true) // Handled by AppUser entity
    @Mapping(target = "invoices", ignore = true) // Handled by Invoice entity
    Employee toEntity(EmployeeRequestDTO employeeRequestDTO);

    // Helper for updating existing entity from DTO
    @Mapping(target = "id", ignore = true) // Don't update ID
    @Mapping(target = "createdAt", ignore = true) // Don't update creation timestamp
    @Mapping(target = "updatedAt", ignore = true) // Handled by @UpdateTimestamp
    @Mapping(target = "appUser", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    void updateEntityFromDto(EmployeeRequestDTO dto, @MappingTarget Employee entity);
}