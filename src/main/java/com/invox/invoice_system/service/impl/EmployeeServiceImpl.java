package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.*;
import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.repository.EmployeeRepository;
import com.invox.invoice_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        Employee employee = Employee.builder()
                .name(dto.getName())
                .position(dto.getPosition())
                .build();
        return mapToDTO(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setName(dto.getName());
        employee.setPosition(dto.getPosition());
        return mapToDTO(employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDTO mapToDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getPosition()
        );
    }
}
