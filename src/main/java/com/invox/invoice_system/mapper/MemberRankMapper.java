package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.MemberRank;
import com.invox.invoice_system.dto.MemberRankDTO;
import org.springframework.stereotype.Component; // Quan trọng để Spring quản lý

@Component // Đánh dấu là Spring Component để có thể @Autowired
public class MemberRankMapper {

    // Convert MemberRank Entity to MemberRankDTO
    public MemberRankDTO toDto(MemberRank memberRank) {
        if (memberRank == null) {
            return null;
        }
        MemberRankDTO dto = new MemberRankDTO();
        dto.setId(memberRank.getId());
        dto.setName(memberRank.getName());
        dto.setMinTotalPoints(memberRank.getMinTotalPoints());
        dto.setPointsEarningRate(memberRank.getPointsEarningRate()); // Đảm bảo kiểu BigDecimal khớp
        dto.setDescription(memberRank.getDescription());
        return dto;
    }

    // Convert MemberRankDTO to MemberRank Entity
    public MemberRank toEntity(MemberRankDTO memberRankDTO) {
        if (memberRankDTO == null) {
            return null;
        }
        MemberRank entity = new MemberRank();
        entity.setId(memberRankDTO.getId()); // ID có thể null khi tạo mới
        entity.setName(memberRankDTO.getName());
        entity.setMinTotalPoints(memberRankDTO.getMinTotalPoints());
        entity.setPointsEarningRate(memberRankDTO.getPointsEarningRate()); // Đảm bảo kiểu BigDecimal khớp
        entity.setDescription(memberRankDTO.getDescription());
        // customers collection sẽ được quản lý bởi JPA
        return entity;
    }

    // Phương thức này có thể hữu ích khi cập nhật một Entity hiện có
    public void updateEntityFromDto(MemberRankDTO dto, MemberRank entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setMinTotalPoints(dto.getMinTotalPoints());
        entity.setPointsEarningRate(dto.getPointsEarningRate());
        entity.setDescription(dto.getDescription());
    }
}