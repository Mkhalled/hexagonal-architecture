package com.hexagonal.demo.application.exception;
/**
 * Exception levée lorsqu'une ressource n'est pas trouvée.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String errorCode;
    private final String message;

    public ResourceNotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}