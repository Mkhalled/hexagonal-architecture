package com.hexagonal.demo.domain.service;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .build();
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(product);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(product.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProduct(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(product.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> results = productService.getAllProducts();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo(product.getName());
        verify(productRepository).findAll();
    }

    @Test
    void deleteProduct_ShouldCallRepository() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
}