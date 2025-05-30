package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.EmployeeRequestDTO;
import com.invox.invoice_system.dto.EmployeeResponseDTO;
import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.enums.EmployeeStatus;
import com.invox.invoice_system.mapper.EmployeeMapper;
import com.invox.invoice_system.repository.EmployeeRepository;
import com.invox.invoice_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeResponseDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponseDto);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeRequestDTO) {
        // Kiểm tra số điện thoại và email duy nhất
        if (employeeRequestDTO.getPhone() != null && employeeRepository.findByPhone(employeeRequestDTO.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại nhân viên đã tồn tại: " + employeeRequestDTO.getPhone());
        }
        if (employeeRequestDTO.getEmail() != null && employeeRepository.findByEmail(employeeRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email nhân viên đã tồn tại: " + employeeRequestDTO.getEmail());
        }

        Employee employee = employeeMapper.toEntity(employeeRequestDTO);
        if (employee.getHireDate() == null) {
            employee.setHireDate(LocalDate.now()); // Mặc định ngày hiện tại nếu không cung cấp
        }
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE); // Mặc định là ACTIVE
        }

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + id));

        // Kiểm tra số điện thoại và email duy nhất khi cập nhật
        if (employeeRequestDTO.getPhone() != null) {
            Optional<Employee> employeeWithPhone = employeeRepository.findByPhone(employeeRequestDTO.getPhone());
            if (employeeWithPhone.isPresent() && !employeeWithPhone.get().getId().equals(id)) {
                throw new IllegalArgumentException("Số điện thoại nhân viên đã tồn tại: " + employeeRequestDTO.getPhone());
            }
        }
        if (employeeRequestDTO.getEmail() != null) {
            Optional<Employee> employeeWithEmail = employeeRepository.findByEmail(employeeRequestDTO.getEmail());
            if (employeeWithEmail.isPresent() && !employeeWithEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email nhân viên đã tồn tại: " + employeeRequestDTO.getEmail());
            }
        }

        // Cập nhật các trường từ DTO vào Entity
        employeeMapper.updateEntityFromDto(employeeRequestDTO, existingEmployee);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có AppUser hoặc Invoice liên quan
        // Hoặc thực hiện soft delete thay vì xóa cứng
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO deactivateEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + employeeId));

        employee.setStatus(EmployeeStatus.INACTIVE);
        employee.setTerminationDate(LocalDate.now()); // Ghi nhận ngày nghỉ việc
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }
}