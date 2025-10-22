
package com.hexagonal.demo.application.service;

// Renommé en ProductUseCase

import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service applicatif pour la gestion des produits.
 * Orchestration des cas d'usage métier via le service du domaine.
 */
@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductService productService;

    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }

    public Optional<Product> getProduct(Long id) {
        return productService.getProduct(id);
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public Product updateProduct(Product product) {
        return productService.updateProduct(product);
    }

    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }
}