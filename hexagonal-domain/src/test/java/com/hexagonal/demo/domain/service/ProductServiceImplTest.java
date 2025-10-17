package com.hexagonal.demo.domain.service;

import com.hexagonal.demo.domain.exception.BusinessValidationException;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    private ProductRepository productRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_shouldValidateAndSave() {
        Product product = Product.builder()
                .name("Test")
                .price(BigDecimal.valueOf(10))
                .quantity(5)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product saved = productService.createProduct(product);
        assertThat(saved.getName()).isEqualTo("Test");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_shouldThrowValidationException() {
        Product product = Product.builder().name("").price(BigDecimal.valueOf(-1)).quantity(-1).build();
        assertThatThrownBy(() -> productService.createProduct(product))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    void getProduct_shouldReturnProduct() {
        Product product = Product.builder().id(1L).name("Test").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> result = productService.getProduct(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void getProduct_shouldThrowResourceNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProduct(2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllProducts_shouldReturnList() {
        Product p1 = Product.builder().id(1L).build();
        Product p2 = Product.builder().id(2L).build();
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        List<Product> products = productService.getAllProducts();
        assertThat(products).hasSize(2);
    }

    @Test
    void updateProduct_shouldValidateAndSave() {
        Product product = Product.builder().id(1L).name("Updated").price(BigDecimal.valueOf(20)).quantity(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product updated = productService.updateProduct(product);
        assertThat(updated.getName()).isEqualTo("Updated");
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_shouldDelete() {
        Product product = Product.builder().id(1L).name("ToDelete").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }
}
