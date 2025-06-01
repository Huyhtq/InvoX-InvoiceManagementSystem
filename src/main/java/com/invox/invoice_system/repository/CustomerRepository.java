package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm khách hàng theo số điện thoại (để đăng ký/kiểm tra trùng lặp)
    Optional<Customer> findByPhone(String phone);

    // Tìm khách hàng theo email (để đăng ký/kiểm tra trùng lặp)
    Optional<Customer> findByEmail(String email);

    // Tìm khách hàng theo tên chứa một chuỗi
    List<Customer> findByNameContainingIgnoreCase(String name);

    // Tìm khách hàng có tổng điểm tích lũy lớn hơn hoặc bằng một ngưỡng nào đó (cho hạng thành viên)
    List<Customer> findByTotalPointsGreaterThanEqual(Long totalPoints);

    List<Customer> findByNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);
}