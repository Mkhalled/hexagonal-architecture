package com.hexagonal.demo.domain.service;

import com.hexagonal.demo.domain.exception.BusinessValidationException;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;
import com.hexagonal.demo.domain.ports.spi.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        log.info("Création d'un nouveau produit : {}", product.getName());
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        log.debug("Recherche du produit avec l'ID : {}", id);
        return Optional.ofNullable(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id.toString())));
    }

    @Override
    public List<Product> getAllProducts() {
        log.debug("Récupération de tous les produits");
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product) {
        log.debug("Mise à jour du produit : {}", product.getId());
        getProduct(product.getId()); // Vérifie si le produit existe
        validateProduct(product);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        log.debug("Suppression du produit : {}", id);
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

        log.debug("Validation du produit réussie : {}", product.getName());
    }
}