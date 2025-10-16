package com.hexagonal.demo.infrastructure.adapters.output.persistence.mapper;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductEntity toEntity(Product product);
    Product toDomain(ProductEntity productEntity);
}