package com.hexagonal.demo.infrastructure.adapters.input.rest;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {
    private ProductService productService;
    private ProductController productController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void createProduct_shouldReturnCreated() throws Exception {
        Product product = Product.builder().name("Test").price(BigDecimal.TEN).quantity(1).build();
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"name\": \"Test\"," +
                        "\"price\": 10," +
                        "\"quantity\": 1" +
                        "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void getProduct_shouldReturnProduct() throws Exception {
        Product product = Product.builder().id(1L).name("Test").build();
        when(productService.getProduct(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        Product p1 = Product.builder().id(1L).build();
        Product p2 = Product.builder().id(2L).build();
        when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_shouldReturnOk() throws Exception {
        Product product = Product.builder().id(1L).name("Updated").build();
        when(productService.updateProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"name\": \"Updated\"," +
                        "\"price\": 10," +
                        "\"quantity\": 1" +
                        "}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
