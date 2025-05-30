package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Optional<Employee> findByPhone(String phone);
    Optional<Employee> findByEmail(String email);
}
