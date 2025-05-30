package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.RoleDTO;
import com.invox.invoice_system.entity.Role;
import com.invox.invoice_system.mapper.RoleMapper;
import com.invox.invoice_system.repository.RoleRepository;
import com.invox.invoice_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.findByName(roleDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại: " + roleDTO.getName());
        }
        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò với ID: " + id));

        Optional<Role> roleWithName = roleRepository.findByName(roleDTO.getName());
        if (roleWithName.isPresent() && !roleWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại: " + roleDTO.getName());
        }

        existingRole.setName(roleDTO.getName());
        Role updatedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy vai trò với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có AppUser liên quan
        // if (appUserRepository.countByRoleId(id) > 0) { ... }
        roleRepository.deleteById(id);
    }
}