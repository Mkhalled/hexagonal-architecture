package com.hexagonal.demo.infrastructure.adapters.input.rest;

import com.hexagonal.demo.domain.model.Product;
import com.hexagonal.demo.domain.ports.api.ProductService;

import com.hexagonal.demo.infrastructure.config.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Products", description = "API pour la gestion des produits")
@SecurityRequirement(name = "apiKey")
public class ProductController {

    private final ProductService productService;

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
                description = "Données invalides",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Non authentifié",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Non autorisé",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erreur serveur",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            )
        }
    )
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productService.createProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtenir un produit par son ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Produit trouvé"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Produit non trouvé",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erreur serveur",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            )
        }
    )
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
        summary = "Obtenir tous les produits",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Liste des produits"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erreur serveur",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            )
        }
    )
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un produit",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Produit mis à jour"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Données invalides",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Produit non trouvé",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erreur serveur",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            )
        }
    )
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return ResponseEntity.ok(productService.updateProduct(product));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un produit",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Produit supprimé"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Produit non trouvé",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Erreur serveur",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiError.class)
                )
            )
        }
    )
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}