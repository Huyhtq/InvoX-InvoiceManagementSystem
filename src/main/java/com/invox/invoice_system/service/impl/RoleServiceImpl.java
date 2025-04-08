package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.entity.Role;
import com.invox.invoice_system.service.RoleService;
import com.invox.invoice_system.dto.RoleDTO;
import com.invox.invoice_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleDTO create(RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        return mapToDTO(roleRepository.save(role));
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public RoleDTO getById(Long id) {
        return roleRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    @Override
    public RoleDTO update(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id).orElseThrow();
        role.setName(dto.getName());
        return mapToDTO(roleRepository.save(role));
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    private RoleDTO mapToDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }
}
