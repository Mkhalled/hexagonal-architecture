package com.hexagonal.demo.domain.exception;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée.
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceType, String id) {
        super(
            String.format("%s avec l'identifiant %s n'a pas été trouvé", resourceType, id),
            "RESOURCE_NOT_FOUND"
        );
    }
}