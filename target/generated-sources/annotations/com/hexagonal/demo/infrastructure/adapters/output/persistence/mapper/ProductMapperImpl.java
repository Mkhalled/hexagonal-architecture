package com.hexagonal.demo.infrastructure.adapters.output.persistence.mapper;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.entity.ProductEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-14T22:41:29+0200",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductEntity toEntity(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.description( product.getDescription() );
        productEntity.id( product.getId() );
        productEntity.name( product.getName() );
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

        product.description( productEntity.getDescription() );
        product.id( productEntity.getId() );
        product.name( productEntity.getName() );
        product.price( productEntity.getPrice() );
        product.quantity( productEntity.getQuantity() );

        return product.build();
    }
}
