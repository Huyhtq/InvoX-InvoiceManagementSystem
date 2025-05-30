package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.MemberRank;
import com.invox.invoice_system.dto.MemberRankDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberRankMapper {

    MemberRankMapper INSTANCE = Mappers.getMapper(MemberRankMapper.class);

    MemberRankDTO toDto(MemberRank memberRank);

    // When converting DTO to Entity, audit fields and collections are ignored
    @Mapping(target = "customers", ignore = true)
    MemberRank toEntity(MemberRankDTO memberRankDTO);
}