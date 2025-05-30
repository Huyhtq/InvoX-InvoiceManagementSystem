package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Role;
import com.invox.invoice_system.dto.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDto(Role role);

    @Mapping(target = "appUsers", ignore = true) // Ignore collection
    Role toEntity(RoleDTO roleDTO);
}