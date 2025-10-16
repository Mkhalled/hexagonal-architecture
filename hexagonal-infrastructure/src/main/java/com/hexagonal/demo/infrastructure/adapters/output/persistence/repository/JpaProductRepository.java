package com.hexagonal.demo.infrastructure.adapters.output.persistence.repository;

import com.hexagonal.demo.infrastructure.adapters.output.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
}