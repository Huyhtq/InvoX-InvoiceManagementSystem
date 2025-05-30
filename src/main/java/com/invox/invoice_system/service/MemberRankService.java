package com.invox.invoice_system.service;

import com.invox.invoice_system.dto.MemberRankDTO;
import com.invox.invoice_system.entity.MemberRank;

import java.util.List;
import java.util.Optional;

public interface MemberRankService {
    List<MemberRankDTO> getAllMemberRanks();
    Optional<MemberRankDTO> getMemberRankById(Long id);
    MemberRankDTO createMemberRank(MemberRankDTO memberRankDTO);
    MemberRankDTO updateMemberRank(Long id, MemberRankDTO memberRankDTO);
    void deleteMemberRank(Long id);
    MemberRank getMemberRankForPoints(Long totalPoints); // Internal method, returns entity
}