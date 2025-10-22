package com.hexagonal.demo.controller;

import com.hexagonal.demo.application.service.ProductUseCase;
import com.hexagonal.demo.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API pour la gestion des produits")
@SecurityRequirement(name = "apiKey")
public class ProductController {

    @Autowired
    private ProductUseCase productUseCase;

    @PostMapping
    @Operation(
        summary = "Créer un nouveau produit",
        description = "Crée un nouveau produit avec les informations fournies",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Produit créé avec succès"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Données invalides"
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Non authentifié"
            )
        }
    )
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productUseCase.createProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtenir un produit par son ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Produit trouvé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
        }
    )
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productUseCase.getProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
        summary = "Obtenir tous les produits",
        responses = {
            @ApiResponse(responseCode = "200", description = "Liste des produits")
        }
    )
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productUseCase.getAllProducts());
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un produit",
        responses = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
        }
    )
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return ResponseEntity.ok(productUseCase.updateProduct(product));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un produit",
        responses = {
            @ApiResponse(responseCode = "204", description = "Produit supprimé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
        }
    )
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
