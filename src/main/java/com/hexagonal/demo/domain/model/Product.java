package com.hexagonal.demo.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom du produit doit contenir entre 3 et 100 caractères")
    private String name;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "Le prix du produit est obligatoire")
    @PositiveOrZero(message = "Le prix du produit doit être positif ou nul")
    private BigDecimal price;

    @NotNull(message = "La quantité du produit est obligatoire")
    @PositiveOrZero(message = "La quantité du produit doit être positive ou nulle")
    private Integer quantity;
}