package com.hexagonal.demo.infrastructure.config;

import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import com.hexagonal.demo.domain.service.ProductServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductServiceConfig {
    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductServiceImpl(productRepository);
    }
}
