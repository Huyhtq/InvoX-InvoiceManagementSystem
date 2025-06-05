package com.invox.invoice_system.service.impl;

import com.invox.invoice_system.dto.MemberRankDTO;
import com.invox.invoice_system.entity.MemberRank;
import com.invox.invoice_system.mapper.MemberRankMapper;
import com.invox.invoice_system.repository.MemberRankRepository;
import com.invox.invoice_system.service.MemberRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberRankServiceImpl implements MemberRankService {

    private final MemberRankRepository memberRankRepository;
    private final MemberRankMapper memberRankMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MemberRankDTO> getAllMemberRanks() {
        return memberRankRepository.findAll().stream()
                .map(memberRankMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberRankDTO> getMemberRankById(Long id) {
        return memberRankRepository.findById(id)
                .map(memberRankMapper::toDto);
    }

    @Override
    @Transactional
    public MemberRankDTO createMemberRank(MemberRankDTO memberRankDTO) {
        if (memberRankRepository.findByName(memberRankDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên hạng thành viên đã tồn tại: " + memberRankDTO.getName());
        }
        if (memberRankDTO.getMinTotalPoints() < 0) {
            throw new IllegalArgumentException("Điểm tối thiểu không thể âm.");
        }
        MemberRank memberRank = memberRankMapper.toEntity(memberRankDTO);
        MemberRank savedMemberRank = memberRankRepository.save(memberRank);
        return memberRankMapper.toDto(savedMemberRank);
    }

    @Override
    @Transactional
    public MemberRankDTO updateMemberRank(Long id, MemberRankDTO memberRankDTO) {
        MemberRank existingMemberRank = memberRankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hạng thành viên với ID: " + id));

        Optional<MemberRank> rankWithName = memberRankRepository.findByName(memberRankDTO.getName());
        if (rankWithName.isPresent() && !rankWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên hạng thành viên đã tồn tại: " + memberRankDTO.getName());
        }

        if (memberRankDTO.getMinTotalPoints() < 0) {
            throw new IllegalArgumentException("Điểm tối thiểu không thể âm.");
        }

        existingMemberRank.setName(memberRankDTO.getName());
        existingMemberRank.setMinTotalPoints(memberRankDTO.getMinTotalPoints());
        existingMemberRank.setPointsEarningRate(memberRankDTO.getPointsEarningRate());
        existingMemberRank.setDescription(memberRankDTO.getDescription());

        MemberRank updatedMemberRank = memberRankRepository.save(existingMemberRank);
        return memberRankMapper.toDto(updatedMemberRank);
    }

    @Override
    @Transactional
    public void deleteMemberRank(Long id) {
        if (!memberRankRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy hạng thành viên với ID: " + id);
        }
        // Thêm logic kiểm tra nếu có khách hàng liên quan
        // (ví dụ: chuyển khách hàng về hạng mặc định trước khi xóa hạng này)
        // customerRepository.updateMemberRankToDefault(id); // Nếu có phương thức này
        memberRankRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberRank getMemberRankForPoints(Long totalPoints) {
        // Phương thức này tìm hạng thành viên phù hợp với tổng điểm
        // Giả định bạn có một phương thức trong Repository để lấy rank dựa trên điểm
        // Ví dụ: findTopByMinTotalPointsLessThanEqualOrderByMinTotalPointsDesc
        return memberRankRepository.findAll().stream()
                .filter(rank -> totalPoints >= rank.getMinTotalPoints())
                .max((r1, r2) -> Long.compare(r1.getMinTotalPoints(), r2.getMinTotalPoints()))
                .orElse(null); // Hoặc ném ngoại lệ nếu không có hạng nào phù hợp (không nên xảy ra nếu có hạng "Đồng" với 0 điểm)
    }
}