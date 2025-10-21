package com.hexagonal.demo.controller;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        reset(productService);
    }

    @Test
    void createProduct_shouldReturnCreated() throws Exception {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(1299.99))
                .quantity(5)
                .build();

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Laptop\", \"price\": 1299.99, \"quantity\": 5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void getProduct_shouldReturnProduct() throws Exception {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1299.99))
                .quantity(5)
                .build();

        when(productService.getProduct(1L)).thenReturn(Optional.of(product));

        // When & Then
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void getProduct_notFound_shouldReturn404() throws Exception {
        // Given
        when(productService.getProduct(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProduct(999L);
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        // Given
        Product product1 = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1299.99))
                .quantity(5)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Mouse")
                .price(BigDecimal.valueOf(29.99))
                .quantity(50)
                .build();

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getAllProducts_empty_shouldReturnEmptyList() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void updateProduct_shouldReturnOk() throws Exception {
        // Given
        Product product = Product.builder()
                .id(1L)
                .name("Updated Laptop")
                .price(BigDecimal.valueOf(1199.99))
                .quantity(3)
                .build();

        when(productService.updateProduct(any(Product.class))).thenReturn(product);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Laptop\", \"price\": 1199.99, \"quantity\": 3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Laptop"));

        verify(productService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}
