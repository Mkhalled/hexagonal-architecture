package com.hexagonal.demo.domain.service;

import com.hexagonal.demo.domain.exception.BusinessValidationException;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("ProductRepository cannot be null");
        }
        // Cast vers l'interface immuable pour garantir l'immutabilité
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        validateProduct(createDefensiveCopy(product));
        return createDefensiveCopy(productRepository.save(product));
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return Optional.ofNullable(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id.toString())))
                .map(this::createDefensiveCopy);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::createDefensiveCopy)
                .toList();
    }

    @Override
    public Product updateProduct(Product product) {
        Product defensiveCopy = createDefensiveCopy(product);
        getProduct(defensiveCopy.getId()); // Vérifie si le produit existe
        validateProduct(defensiveCopy);
        return createDefensiveCopy(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        getProduct(id); // Vérifie si le produit existe
        productRepository.deleteById(id);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessValidationException("Le nom du produit est obligatoire");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Le prix du produit doit être positif ou nul");
        }

        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new BusinessValidationException("La quantité du produit doit être positive ou nulle");
        }
    }

    private Product createDefensiveCopy(Product product) {
        if (product == null) {
            return null;
        }
        return Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice() != null ? new BigDecimal(product.getPrice().toString()) : null)
                .quantity(product.getQuantity())
                .build();
    }
}