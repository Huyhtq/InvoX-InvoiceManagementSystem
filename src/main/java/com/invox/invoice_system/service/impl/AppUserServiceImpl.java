package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.UserRegistrationDTO;
import com.invox.invoice_system.dto.UserResponseDTO;
import com.invox.invoice_system.entity.AppUser;
import com.invox.invoice_system.entity.Employee;
import com.invox.invoice_system.entity.Role;
import com.invox.invoice_system.mapper.AppUserMapper;
import com.invox.invoice_system.repository.AppUserRepository;
import com.invox.invoice_system.repository.EmployeeRepository; 
import com.invox.invoice_system.repository.RoleRepository;
import com.invox.invoice_system.service.AppUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService,UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder; 

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm AppUser từ database
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tìm thấy với tên đăng nhập: " + username));

        // 2. Chuyển đổi vai trò (Role) của AppUser thành GrantedAuthority của Spring Security
        // Spring Security yêu cầu các vai trò phải bắt đầu bằng tiền tố "ROLE_"
        // Đảm bảo tên vai trò trong database của bạn (ví dụ: 'ROLE_ADMIN') đã có tiền tố này
        // Nếu tên vai trò trong DB KHÔNG có tiền tố "ROLE_", bạn cần thêm nó vào đây:
        // Ví dụ: List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().getName()))
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(appUser.getRole().getName())
        );
        // Hoặc nếu role là danh sách nhiều vai trò:
        // List<GrantedAuthority> authorities = appUser.getRoles().stream()
        //     .map(role -> new SimpleGrantedAuthority(role.getName()))
        //     .collect(Collectors.toList());

        // 3. Trả về đối tượng UserDetails của Spring Security
        // Đây là đối tượng User mặc định của Spring Security.
        // Nó chứa username, password (đã băm), và danh sách các quyền/vai trò.
        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),        // Username (tên đăng nhập)
                appUser.getPassword(),        // Mật khẩu (đã băm từ DB)
                authorities                   // Danh sách các quyền/vai trò
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllAppUsers() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> getAppUserById(Long id) {
        return appUserRepository.findById(id)
                .map(appUserMapper::toResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDTO registerNewUser(UserRegistrationDTO userRegistrationDTO) {
        // Kiểm tra username duy nhất
        if (appUserRepository.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại: " + userRegistrationDTO.getUsername());
        }

        // Kiểm tra employeeId tồn tại và chưa có tài khoản
        Employee employee = employeeRepository.findById(userRegistrationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + userRegistrationDTO.getEmployeeId()));
        if (employee.getAppUser() != null) { // Kiểm tra nhân viên đã có tài khoản chưa
            throw new IllegalArgumentException("Nhân viên này đã có tài khoản người dùng.");
        }

        // Kiểm tra roleId tồn tại
        Role role = roleRepository.findById(userRegistrationDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + userRegistrationDTO.getRoleId()));

        AppUser appUser = appUserMapper.toEntity(userRegistrationDTO);
        appUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword())); // Mã hóa mật khẩu
        appUser.setEmployee(employee);
        appUser.setRole(role);

        AppUser savedAppUser = appUserRepository.save(appUser);
        return appUserMapper.toResponseDto(savedAppUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateAppUser(Long id, UserRegistrationDTO userRegistrationDTO) {
        AppUser existingAppUser = appUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        // Kiểm tra username duy nhất khi cập nhật
        Optional<AppUser> userWithUsername = appUserRepository.findByUsername(userRegistrationDTO.getUsername());
        if (userWithUsername.isPresent() && !userWithUsername.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại: " + userRegistrationDTO.getUsername());
        }

        if (userRegistrationDTO.getEmployeeId() != null && !userRegistrationDTO.getEmployeeId().equals(existingAppUser.getEmployee().getId())) {
             Employee newEmployee = employeeRepository.findById(userRegistrationDTO.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + userRegistrationDTO.getEmployeeId()));
             if (newEmployee.getAppUser() != null && !newEmployee.getId().equals(existingAppUser.getEmployee().getId())) {
                 throw new IllegalArgumentException("Nhân viên mới đã có tài khoản người dùng.");
             }
             existingAppUser.setEmployee(newEmployee);
        }

        if (userRegistrationDTO.getRoleId() != null && !userRegistrationDTO.getRoleId().equals(existingAppUser.getRole().getId())) {
            Role newRole = roleRepository.findById(userRegistrationDTO.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + userRegistrationDTO.getRoleId()));
            existingAppUser.setRole(newRole);
        }

        existingAppUser.setUsername(userRegistrationDTO.getUsername());

        AppUser updatedAppUser = appUserRepository.save(existingAppUser);
        return appUserMapper.toResponseDto(updatedAppUser);
    }

    @Override
    @Transactional
    public void deleteAppUser(Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có History liên quan (hoặc chỉ xóa mềm)
        appUserRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        // Phương thức này thường được dùng bởi Spring Security UserDetailsService
        return appUserRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));

        appUser.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(appUser);
    }
}