package com.hexagonal.demo.domain.exception;

/**
 * Exception levée lors d'une validation métier échouée.
 */
public class BusinessValidationException extends BusinessException {
    public BusinessValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }

    public BusinessValidationException(String message, String code) {
        super(message, code);
    }
}