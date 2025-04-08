package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.RoleDTO;
import com.invox.invoice_system.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

