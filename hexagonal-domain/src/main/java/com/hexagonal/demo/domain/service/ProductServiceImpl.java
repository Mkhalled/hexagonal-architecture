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
        validateProduct(product);
        return productRepository.save(product);
    }

    @Override

    public Optional<Product> getProduct(Long id) {
        return Optional.ofNullable(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id.toString())));
    }

    @Override

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override

    public Product updateProduct(Product product) {
        getProduct(product.getId()); // Vérifie si le produit existe
        validateProduct(product);
        return productRepository.save(product);
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


    }