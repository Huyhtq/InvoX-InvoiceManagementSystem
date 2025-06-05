package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long>{
    Optional<Category> findByName(String name);
}
