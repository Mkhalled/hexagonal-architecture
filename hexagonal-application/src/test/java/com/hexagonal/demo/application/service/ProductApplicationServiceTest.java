package com.hexagonal.demo.application.service;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductApplicationService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductApplicationService(productRepository);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = productService.createProduct(product);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        verify(productRepository).save(product);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.getProduct(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(updatedProduct);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Arrange
        Long productId = 1L;

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository).deleteById(productId);
    }
}