package com.hexagonal.demo.infrastructure.adapters.output.persistence;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.entity.ProductEntity;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.mapper.ProductMapper;
import com.hexagonal.demo.infrastructure.adapters.output.persistence.repository.JpaProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductPersistenceAdapterTest {

    @Mock
    private JpaProductRepository jpaProductRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductPersistenceAdapter productPersistenceAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productPersistenceAdapter = new ProductPersistenceAdapter(jpaProductRepository, productMapper);
    }

    @Test
    void testSaveProduct() {
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100))
                .quantity(10)
                .build();

        ProductEntity entity = new ProductEntity();
        entity.setName("Test Product");
        entity.setDescription("Test Description");
        entity.setPrice(BigDecimal.valueOf(100));
        entity.setQuantity(10);

        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Test Product");
        savedEntity.setDescription("Test Description");
        savedEntity.setPrice(BigDecimal.valueOf(100));
        savedEntity.setQuantity(10);

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(100))
                .quantity(10)
                .build();

        when(productMapper.toEntity(product)).thenReturn(entity);
        when(jpaProductRepository.save(entity)).thenReturn(savedEntity);
        when(productMapper.toDomain(savedEntity)).thenReturn(savedProduct);

        Product result = productPersistenceAdapter.save(product);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productMapper, times(1)).toEntity(product);
        verify(jpaProductRepository, times(1)).save(entity);
        verify(productMapper, times(1)).toDomain(savedEntity);
    }

    @Test
    void testFindByIdExists() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test Product");

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productMapper.toDomain(entity)).thenReturn(product);

        Optional<Product> result = productPersistenceAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(jpaProductRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotExists() {
        when(jpaProductRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productPersistenceAdapter.findById(999L);

        assertThat(result).isEmpty();
        verify(jpaProductRepository, times(1)).findById(999L);
        verify(productMapper, never()).toDomain(any());
    }

    @Test
    void testFindAll() {
        ProductEntity entity1 = new ProductEntity();
        entity1.setId(1L);
        entity1.setName("Product 1");

        ProductEntity entity2 = new ProductEntity();
        entity2.setId(2L);
        entity2.setName("Product 2");

        List<ProductEntity> entities = Arrays.asList(entity1, entity2);

        Product product1 = Product.builder().id(1L).name("Product 1").build();
        Product product2 = Product.builder().id(2L).name("Product 2").build();

        when(jpaProductRepository.findAll()).thenReturn(entities);
        when(productMapper.toDomain(entity1)).thenReturn(product1);
        when(productMapper.toDomain(entity2)).thenReturn(product2);

        List<Product> result = productPersistenceAdapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactly("Product 1", "Product 2");
        verify(jpaProductRepository, times(1)).findAll();
        verify(productMapper, times(2)).toDomain(any());
    }

    @Test
    void testDeleteById() {
        productPersistenceAdapter.deleteById(1L);

        verify(jpaProductRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAllEmpty() {
        when(jpaProductRepository.findAll()).thenReturn(Arrays.asList());

        List<Product> result = productPersistenceAdapter.findAll();

        assertThat(result).isEmpty();
        verify(jpaProductRepository, times(1)).findAll();
    }
}
