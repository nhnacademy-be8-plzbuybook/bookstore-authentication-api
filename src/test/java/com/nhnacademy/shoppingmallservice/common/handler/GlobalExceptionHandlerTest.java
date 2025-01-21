package com.nhnacademy.shoppingmallservice.common.handler;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.common.exception.NotRegisteredException;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void testNotFoundExceptionHandler() {
        // Given
        NotFoundException ex = new NotFoundException("Not Found");
        URI expectedType = URI.create("/signup");

        // When
        ResponseEntity<?> response = globalExceptionHandler.memberNotFoundHandler(ex, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(expectedType, ((ProblemDetail) response.getBody()).getType());
    }

    @Test
    void testUnAuthorizedExceptionHandler() {
        // Given
        UnAuthorizedException ex = new UnAuthorizedException("Unauthorized");
        URI expectedType = URI.create("/api/login");

        // When
        ResponseEntity<?> response = globalExceptionHandler.unAuthorizedHandler(ex, webRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedType, ((ProblemDetail) response.getBody()).getType());
    }

    @Test
    void testNotRegisteredExceptionHandler() {
        // Given
        NotRegisteredException ex = new NotRegisteredException("Not Registered");
        URI expectedType = URI.create("/signup");

        // When
        ResponseEntity<?> response = globalExceptionHandler.notRegisteredHandler(ex, webRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(expectedType, ((ProblemDetail) response.getBody()).getType());
    }

    @Test
    void testMethodArgumentNotValidExceptionHandler() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(ex, headers, status, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}