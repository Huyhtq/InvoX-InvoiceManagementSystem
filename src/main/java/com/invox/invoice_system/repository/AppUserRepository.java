package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Tìm người dùng theo username (bắt buộc cho đăng nhập)
    Optional<AppUser> findByUsername(String username);

    // Tìm người dùng theo ID nhân viên liên kết
    Optional<AppUser> findByEmployeeId(Long employeeId);
}