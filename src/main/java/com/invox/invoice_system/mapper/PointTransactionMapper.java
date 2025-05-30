package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.PointTransaction;
import com.invox.invoice_system.dto.PointTransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class}) // Uses CustomerMapper for nested CustomerDTO
public interface PointTransactionMapper {

    PointTransactionMapper INSTANCE = Mappers.getMapper(PointTransactionMapper.class);

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "invoice.id", target = "invoiceId") // Map only invoice ID to DTO
    PointTransactionResponseDTO toResponseDto(PointTransaction pointTransaction);
}