package com.hexagonal.demo.application.service;

import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import com.hexagonal.demo.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service applicatif pour la gestion des produits.
 * Orchestre les interactions entre le domaine et l'infrastructure.
 */
@Service
@Transactional
public class ProductApplicationService implements ProductService {

    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("ProductRepository cannot be null");
        }
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        // Validation métier effectuée dans le domaine
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product) {
        // La validation de l'existence du produit est effectuée dans le domaine
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        // La validation de l'existence du produit est effectuée dans le domaine
        productRepository.deleteById(id);
    }
}