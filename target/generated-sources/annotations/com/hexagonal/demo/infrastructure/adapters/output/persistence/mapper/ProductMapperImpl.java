package com.hexagonal.demo.infrastructure.adapters.output.persistence.mapper;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.entity.ProductEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-15T06:58:22+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductEntity toEntity(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.id( product.getId() );
        productEntity.name( product.getName() );
        productEntity.description( product.getDescription() );
        productEntity.price( product.getPrice() );
        productEntity.quantity( product.getQuantity() );

        return productEntity.build();
    }

    @Override
    public Product toDomain(ProductEntity productEntity) {
        if ( productEntity == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.id( productEntity.getId() );
        product.name( productEntity.getName() );
        product.description( productEntity.getDescription() );
        product.price( productEntity.getPrice() );
        product.quantity( productEntity.getQuantity() );

        return product.build();
    }
}
