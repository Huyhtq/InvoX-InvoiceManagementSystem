package com.invox.invoice_system.repository;

import com.invox.invoice_system.entity.MemberRank;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRankRepository extends JpaRepository<MemberRank, Long>{
    Optional<MemberRank> findByName(String name);
}