package com.hexagonal.demo.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApiKeyAuthFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ApiKeyAuthFilter apiKeyAuthFilter;
    private ApiKeyProperties apiKeyProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiKeyProperties = new ApiKeyProperties();
        apiKeyProperties.setApiKeys(Arrays.asList("valid-api-key", "another-key"));
        apiKeyAuthFilter = new ApiKeyAuthFilter(apiKeyProperties);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidApiKey() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/products");
        when(request.getHeader("X-API-KEY")).thenReturn("valid-api-key");

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("API_CLIENT");
    }

    @Test
    void testInvalidApiKey() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/products");
        when(request.getHeader("X-API-KEY")).thenReturn("invalid-key");

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testMissingApiKey() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/products");
        when(request.getHeader("X-API-KEY")).thenReturn(null);

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(filterChain, never()).doFilter(request, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/api/swagger-ui.html",
            "/api/v3/api-docs"
    })
    void testExemptedPathsDoNotRequireApiKey(String exemptedPath) throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn(exemptedPath);

        // When
        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
