package com.ecommerce.monolith.mapper;

import com.ecommerce.monolith.dto.ProductDto;
import com.ecommerce.monolith.product.model.Product; // "product" au lieu de "model"
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
    Product toEntity(ProductDto productDto);
}