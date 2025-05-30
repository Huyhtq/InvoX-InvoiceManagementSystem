package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Customer;
import com.invox.invoice_system.entity.MemberRank; // Import for mapping ID
import com.invox.invoice_system.dto.CustomerRequestDTO;
import com.invox.invoice_system.dto.CustomerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MemberRankMapper.class})
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "memberRank", target = "memberRank")
    CustomerResponseDTO toResponseDto(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "totalPoints", ignore = true) // Handled by service/default
    @Mapping(target = "availablePoints", ignore = true) // Handled by service/default
    @Mapping(target = "memberRank", ignore = true) // Handled by service/default or initial assignment
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "pointTransactions", ignore = true)
    Customer toEntity(CustomerRequestDTO customerRequestDTO);

    // Helper method to map MemberRank ID to MemberRank entity if needed (e.g., for initial customer creation)
    default MemberRank toMemberRankEntity(Long memberRankId) {
        if (memberRankId == null) {
            return null;
        }
        MemberRank memberRank = new MemberRank();
        memberRank.setId(memberRankId);
        return memberRank;
    }
}