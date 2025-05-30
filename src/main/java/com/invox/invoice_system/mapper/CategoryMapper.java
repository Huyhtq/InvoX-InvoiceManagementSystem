package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Category;
import com.invox.invoice_system.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Make this a Spring component
public interface CategoryMapper {

    // Instance for manual use if not using Spring component model
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Convert Category Entity to CategoryDTO
    CategoryDTO toDto(Category category);

    // Convert CategoryDTO to Category Entity
    Category toEntity(CategoryDTO categoryDTO);
}