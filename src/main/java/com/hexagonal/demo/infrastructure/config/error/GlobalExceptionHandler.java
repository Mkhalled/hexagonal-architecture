package com.hexagonal.demo.infrastructure.config.error;

import com.hexagonal.demo.domain.exception.BusinessException;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import com.hexagonal.demo.domain.exception.BusinessValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'application.
 * Cette classe intercepte différents types d'exceptions et les convertit en réponses HTTP appropriées.
 * Elle utilise le pattern ControllerAdvice de Spring pour gérer les exceptions de manière centralisée.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions de type ResourceNotFoundException.
     * Ces exceptions sont levées lorsqu'une ressource demandée n'est pas trouvée.
     *
     * @param ex      L'exception ResourceNotFoundException qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 404 (NOT_FOUND) avec les détails de l'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(

            ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions de type BusinessValidationException.
     * Ces exceptions sont levées lors de la validation des règles métier.
     *
     * @param ex      L'exception BusinessValidationException qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 400 (BAD_REQUEST) avec les détails de l'erreur
     */
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiError> handleBusinessValidationException(
            BusinessValidationException ex,
            HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les exceptions de type BusinessException.
     * Ces exceptions sont levées lors d'erreurs métier génériques.
     *
     * @param ex      L'exception BusinessException qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 500 (INTERNAL_SERVER_ERROR) avec les détails de l'erreur
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        log.error("Business error: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * Gère les exceptions de type MethodArgumentNotValidException.
     * Ces exceptions sont levées lors de la validation des paramètres de requête.
     * Collecte toutes les erreurs de validation et les retourne dans la réponse.
     *
     * @param ex      L'exception MethodArgumentNotValidException qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 400 (BAD_REQUEST) avec la liste des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        
        String message = "Erreur de validation: " + errors;
        log.error(message);
        
        ApiError error = ApiError.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les exceptions de type AccessDeniedException.
     * Ces exceptions sont levées lorsqu'un utilisateur tente d'accéder à une ressource
     * pour laquelle il n'a pas les autorisations nécessaires.
     *
     * @param ex      L'exception AccessDeniedException qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 403 (FORBIDDEN) avec les détails de l'erreur
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        
        ApiError error = ApiError.builder()
                .code("ACCESS_DENIED")
                .message("Accès refusé")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Gère toutes les autres exceptions non spécifiquement traitées.
     * C'est le gestionnaire par défaut qui capture les exceptions inattendues.
     *
     * @param ex      L'exception générique qui a été levée
     * @param request La requête HTTP qui a déclenché l'exception
     * @return Une réponse HTTP 500 (INTERNAL_SERVER_ERROR) avec un message d'erreur générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknownException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error:", ex);
        
        ApiError error = ApiError.builder()
                .code("INTERNAL_ERROR")
                .message("Une erreur inattendue s'est produite")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}