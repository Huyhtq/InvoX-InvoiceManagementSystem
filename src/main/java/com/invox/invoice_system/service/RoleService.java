package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.RoleDTO;
import java.util.*;

public interface RoleService {
    RoleDTO create(RoleDTO dto);
    List<RoleDTO> getAll();
    RoleDTO getById(Long id);
    RoleDTO update(Long id, RoleDTO dto);
    void delete(Long id);
}

