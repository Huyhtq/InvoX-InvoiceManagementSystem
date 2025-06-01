package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.RoleDTO;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    Optional<RoleDTO> getRoleById(Long id);
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO updateRole(Long id, RoleDTO roleDTO);
    void deleteRole(Long id);
}