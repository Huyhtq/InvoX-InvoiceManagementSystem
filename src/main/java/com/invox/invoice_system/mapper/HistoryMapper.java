package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.History;
import com.invox.invoice_system.dto.HistoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    HistoryMapper INSTANCE = Mappers.getMapper(HistoryMapper.class);

    @Mapping(source = "user.id", target = "userId") // Map only user ID to DTO
    HistoryResponseDTO toResponseDto(History history);
}