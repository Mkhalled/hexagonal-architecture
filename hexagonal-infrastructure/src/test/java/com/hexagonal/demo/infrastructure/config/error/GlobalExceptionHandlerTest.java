package com.hexagonal.demo.infrastructure.config.error;

import com.hexagonal.demo.domain.exception.BusinessException;
import com.hexagonal.demo.domain.exception.BusinessValidationException;
import com.hexagonal.demo.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/products/1");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404 status")
    void testHandleResourceNotFoundException() {
        // Given
        String resourceType = "Product";
        String id = "1";
        ResourceNotFoundException ex = new ResourceNotFoundException(resourceType, id);

        // When
        ResponseEntity<ApiError> response = handler.handleResourceNotFoundException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("RESOURCE_NOT_FOUND", error.getCode());
        assertTrue(error.getMessage().contains(resourceType));
        assertTrue(error.getMessage().contains(id));
        assertEquals(404, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should handle BusinessValidationException with 400 status")
    void testHandleBusinessValidationException() {
        // Given
        String errorMessage = "Price must be greater than zero";
        BusinessValidationException ex = new BusinessValidationException(errorMessage, "INVALID_PRICE");

        // When
        ResponseEntity<ApiError> response = handler.handleBusinessValidationException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("INVALID_PRICE", error.getCode());
        assertEquals(errorMessage, error.getMessage());
        assertEquals(400, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should handle BusinessException with 500 status")
    void testHandleBusinessException() {
        // Given
        String errorMessage = "An unexpected business error occurred";
        BusinessException ex = new BusinessException(errorMessage, "BUSINESS_ERROR");

        // When
        ResponseEntity<ApiError> response = handler.handleBusinessException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("BUSINESS_ERROR", error.getCode());
        assertEquals(errorMessage, error.getMessage());
        assertEquals(500, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400 status")
    void testHandleValidationExceptions() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        
        var fieldError = mock(org.springframework.validation.FieldError.class);
        when(fieldError.getField()).thenReturn("price");
        when(fieldError.getDefaultMessage()).thenReturn("must not be null");
        
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When
        ResponseEntity<ApiError> response = handler.handleValidationExceptions(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertTrue(error.getMessage().contains("price"));
        assertEquals(400, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should handle AccessDeniedException with 403 status")
    void testHandleAccessDeniedException() {
        // Given
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<ApiError> response = handler.handleAccessDeniedException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("ACCESS_DENIED", error.getCode());
        assertEquals("Accès refusé", error.getMessage());
        assertEquals(403, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void testHandleUnknownException() {
        // Given
        Exception ex = new Exception("Unexpected error");

        // When
        ResponseEntity<ApiError> response = handler.handleUnknownException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("INTERNAL_ERROR", error.getCode());
        assertEquals("Une erreur inattendue s'est produite", error.getMessage());
        assertEquals(500, error.getStatus());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should verify ApiError contains all required fields")
    void testApiErrorFields() {
        // Given
        String errorCode = "TEST_ERROR";
        String errorMessage = "Test error message";
        ResourceNotFoundException ex = new ResourceNotFoundException(errorCode, errorMessage);

        // When
        ResponseEntity<ApiError> response = handler.handleResourceNotFoundException(ex, mockRequest);
        ApiError error = response.getBody();

        // Then
        assertNotNull(error);
        assertNotNull(error.getCode());
        assertNotNull(error.getMessage());
        assertNotNull(error.getStatus());
        assertNotNull(error.getPath());
        assertNotNull(error.getTimestamp());
        assertTrue(error.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should handle BusinessValidationException with null code gracefully")
    void testHandleBusinessValidationExceptionWithNullCode() {
        // Given
        String errorMessage = "Validation failed";
        BusinessValidationException ex = new BusinessValidationException(errorMessage);

        // When
        ResponseEntity<ApiError> response = handler.handleBusinessValidationException(ex, mockRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals("VALIDATION_ERROR", error.getCode());
        assertEquals(errorMessage, error.getMessage());
    }

    @Test
    @DisplayName("Should use correct path from request")
    void testCorrectPathInResponse() {
        // Given
        String customPath = "/api/v1/products/custom";
        when(mockRequest.getRequestURI()).thenReturn(customPath);
        ResourceNotFoundException ex = new ResourceNotFoundException("NOT_FOUND", "Not found");

        // When
        ResponseEntity<ApiError> response = handler.handleResourceNotFoundException(ex, mockRequest);

        // Then
        ApiError error = response.getBody();
        assertNotNull(error);
        assertEquals(customPath, error.getPath());
    }
}
