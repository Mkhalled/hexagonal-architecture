
package com.hexagonal.demo.application.service;

// Renommé en ProductUseCase

import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Service applicatif pour la gestion des produits.
 * Orchestration des cas d'usage métier via le service du domaine.
 */
public class ProductUseCase implements ProductService {

    private final ProductService productService;

    public ProductUseCase(ProductService productService) {
        if (productService == null) {
            throw new IllegalArgumentException("ProductService cannot be null");
        }
        this.productService = productService;
    }

    @Override
    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return productService.getProduct(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public Product updateProduct(Product product) {
        return productService.updateProduct(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }
}