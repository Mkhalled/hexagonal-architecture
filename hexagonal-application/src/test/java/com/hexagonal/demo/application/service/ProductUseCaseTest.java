package com.hexagonal.demo.application.service;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;
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
class ProductUseCaseTest {
    @Mock
    private ProductService productService;
    private ProductUseCase applicationService;

    @BeforeEach
    void setUp() {
    applicationService = new ProductUseCase(productService);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = applicationService.createProduct(product);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        verify(productService).createProduct(product);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        // Act
        List<Product> result = applicationService.getAllProducts();

        // Assert
        assertThat(result).hasSize(2);
        verify(productService).getAllProducts();
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setId(1L);

        when(productService.getProduct(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = applicationService.getProduct(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(productService).getProduct(1L);
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");

        when(productService.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = applicationService.updateProduct(updatedProduct);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");
        verify(productService).updateProduct(updatedProduct);
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Arrange
        Long productId = 1L;

        // Act
        applicationService.deleteProduct(productId);

        // Assert
        verify(productService).deleteProduct(productId);
    }
}