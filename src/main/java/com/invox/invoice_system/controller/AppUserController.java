package com.invox.invoice_system.controller;

import com.invox.invoice_system.dto.AppUserRequestDTO;
import com.invox.invoice_system.dto.AppUserResponseDTO;
import com.invox.invoice_system.dto.AppUserLoginDTO;
import com.invox.invoice_system.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping
    public ResponseEntity<AppUserResponseDTO> createUser(@RequestBody AppUserRequestDTO dto) {
        return ResponseEntity.ok(appUserService.createUser(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(appUserService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(appUserService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUserResponseDTO> updateUser(@PathVariable Long id, @RequestBody AppUserRequestDTO dto) {
        return ResponseEntity.ok(appUserService.updateUser(id, dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AppUserResponseDTO> login(@RequestBody AppUserLoginDTO dto) {
        return ResponseEntity.ok(appUserService.login(dto));
    }
}
