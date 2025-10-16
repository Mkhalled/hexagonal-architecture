package com.hexagonal.demo.domain.ports.spi;

/**
 * Interface immuable pour le repository de produits.
 */
public interface ImmutableProductRepository extends ProductRepository {
    // Cette interface hérite de ProductRepository mais ne peut pas être modifiée
    // car elle n'ajoute aucune nouvelle méthode
}