package com.invox.invoice_system.mapper;

import com.invox.invoice_system.entity.Product;
import com.invox.invoice_system.entity.Category; // Import Category entity
import com.invox.invoice_system.dto.ProductRequestDTO;
import com.invox.invoice_system.dto.ProductResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class}) // uses = {CategoryMapper.class} tells MapStruct how to map Category/CategoryDTO
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // Convert Product Entity to ProductResponseDTO
    // Map category field in Product to category DTO in ProductResponseDTO
    @Mapping(source = "category", target = "category")
    ProductResponseDTO toResponseDto(Product product);

    // Convert ProductRequestDTO to Product Entity
    // Map categoryId from ProductRequestDTO to category entity (only its ID) in Product
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "id", ignore = true) // Ignore ID when mapping to new entity
    @Mapping(target = "createdAt", ignore = true) // Ignore audit fields
    @Mapping(target = "updatedAt", ignore = true) // Ignore audit fields
    @Mapping(target = "invoiceDetails", ignore = true) // Ignore collections not handled here
    Product toEntity(ProductRequestDTO productRequestDTO);

    // Helper method to map Category ID to Category entity if needed elsewhere
    default Category toCategoryEntity(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}