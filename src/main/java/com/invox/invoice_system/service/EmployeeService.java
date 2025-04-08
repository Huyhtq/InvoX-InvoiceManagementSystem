package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO getEmployeeById(Long id);
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto);
    void deleteEmployee(Long id);
}
