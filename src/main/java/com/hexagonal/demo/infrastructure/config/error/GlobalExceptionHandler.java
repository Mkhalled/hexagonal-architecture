package com.hexagonal.demo.infrastructure.config.error;package com.hexagonal.demo.infrastructure.config.error;



import com.hexagonal.demo.domain.exception.BusinessException;import com.hexagonal.demo.domain.exception.BusinessException;

import com.hexagonal.demo.domain.exception.ResourceNotFoundException;import com.hexagonal.demo.domain.exception.ResourceNotFoundException;

import com.hexagonal.demo.domain.exception.BusinessValidationException;import com.hexagonal.demo.domain.exception.BusinessValidationException;



import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ConstraintViolationException;import jakarta.validation.ConstraintViolationException;



import lombok.extern.slf4j.Slf4j;import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.MethodArgumentNotValidException;



import java.time.LocalDateTime;import java.time.LocalDateTime;

import java.util.HashMap;import java.util.HashMap;

import java.util.Map;import java.util.Map;



@Slf4j@Slf4j

@RestControllerAdvice@RestControllerAdvice

public class GlobalExceptionHandler {public class GlobalExceptionHandler {



    @ExceptionHandler(ResourceNotFoundException.class)    @ExceptionHandler(ResourceNotFoundException.class)

    public ResponseEntity<ApiError> handleResourceNotFoundException(    public ResponseEntity&lt;ApiError&gt; handleResourceNotFoundException(

            ResourceNotFoundException ex,            ResourceNotFoundException ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        log.error("Resource not found: {}", ex.getMessage());        log.error("Resource not found: {}", ex.getMessage());

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code(ex.getCode())                .code(ex.getCode())

                .message(ex.getMessage())                .message(ex.getMessage())

                .status(HttpStatus.NOT_FOUND.value())                .status(HttpStatus.NOT_FOUND.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);        return new ResponseEntity&lt;&gt;(error, HttpStatus.NOT_FOUND);

    }    }



    @ExceptionHandler(BusinessValidationException.class)    @ExceptionHandler(BusinessValidationException.class)

    public ResponseEntity<ApiError> handleBusinessValidationException(    public ResponseEntity&lt;ApiError&gt; handleBusinessValidationException(

            BusinessValidationException ex,            BusinessValidationException ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        log.error("Validation error: {}", ex.getMessage());        log.error("Validation error: {}", ex.getMessage());

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code(ex.getCode())                .code(ex.getCode())

                .message(ex.getMessage())                .message(ex.getMessage())

                .status(HttpStatus.BAD_REQUEST.value())                .status(HttpStatus.BAD_REQUEST.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);        return new ResponseEntity&lt;&gt;(error, HttpStatus.BAD_REQUEST);

    }    }



    @ExceptionHandler(BusinessException.class)    @ExceptionHandler(BusinessException.class)

    public ResponseEntity<ApiError> handleBusinessException(    public ResponseEntity&lt;ApiError&gt; handleBusinessException(

            BusinessException ex,            BusinessException ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        log.error("Business error: {}", ex.getMessage());        log.error("Business error: {}", ex.getMessage());

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code(ex.getCode())                .code(ex.getCode())

                .message(ex.getMessage())                .message(ex.getMessage())

                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);        return new ResponseEntity&lt;&gt;(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }    }



    @ExceptionHandler(MethodArgumentNotValidException.class)    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<ApiError> handleValidationExceptions(    public ResponseEntity&lt;ApiError&gt; handleValidationExceptions(

            MethodArgumentNotValidException ex,            MethodArgumentNotValidException ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();        Map&lt;String, String&gt; errors = new HashMap&lt;&gt;();

        ex.getBindingResult().getFieldErrors().forEach(error ->        ex.getBindingResult().getFieldErrors().forEach(error ->

            errors.put(error.getField(), error.getDefaultMessage()));            errors.put(error.getField(), error.getDefaultMessage()));

                        

        String message = "Erreur de validation: " + errors;        String message = "Erreur de validation: " + errors;

        log.error(message);        log.error(message);

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code("VALIDATION_ERROR")                .code("VALIDATION_ERROR")

                .message(message)                .message(message)

                .status(HttpStatus.BAD_REQUEST.value())                .status(HttpStatus.BAD_REQUEST.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);        return new ResponseEntity&lt;&gt;(error, HttpStatus.BAD_REQUEST);

    }    }



    @ExceptionHandler(AccessDeniedException.class)    @ExceptionHandler(AccessDeniedException.class)

    public ResponseEntity<ApiError> handleAccessDeniedException(    public ResponseEntity&lt;ApiError&gt; handleAccessDeniedException(

            AccessDeniedException ex,            AccessDeniedException ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        log.error("Access denied: {}", ex.getMessage());        log.error("Access denied: {}", ex.getMessage());

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code("ACCESS_DENIED")                .code("ACCESS_DENIED")

                .message("Accès refusé")                .message("Accès refusé")

                .status(HttpStatus.FORBIDDEN.value())                .status(HttpStatus.FORBIDDEN.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);        return new ResponseEntity&lt;&gt;(error, HttpStatus.FORBIDDEN);

    }    }



    @ExceptionHandler(Exception.class)    @ExceptionHandler(Exception.class)

    public ResponseEntity<ApiError> handleUnknownException(    public ResponseEntity&lt;ApiError&gt; handleUnknownException(

            Exception ex,            Exception ex,

            HttpServletRequest request) {            HttpServletRequest request) {

        log.error("Unexpected error:", ex);        log.error("Unexpected error:", ex);

                

        ApiError error = ApiError.builder()        ApiError error = ApiError.builder()

                .code("INTERNAL_ERROR")                .code("INTERNAL_ERROR")

                .message("Une erreur inattendue s'est produite")                .message("Une erreur inattendue s'est produite")

                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())

                .path(request.getRequestURI())                .path(request.getRequestURI())

                .timestamp(LocalDateTime.now())                .timestamp(LocalDateTime.now())

                .build();                .build();

                                

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);        return new ResponseEntity&lt;&gt;(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }    }

}}