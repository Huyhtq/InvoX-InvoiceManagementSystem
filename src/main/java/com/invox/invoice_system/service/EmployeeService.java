package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
    Optional<EmployeeResponseDTO> getEmployeeById(Long id);
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO);
    void deleteEmployee(Long id);
    EmployeeResponseDTO deactivateEmployee(Long employeeId);
}