package com.nhnacademy.shoppingmallservice.common.handler;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.common.exception.NotRegisteredException;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> memberNotFoundHandler(Exception ex, WebRequest request) {
        String detail = ex.getMessage();
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        URI type = URI.create("/signup");

        ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
        body.setType(type);

        return ResponseEntity.of(body).build();
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity<?> unAuthorizedHandler(Exception ex, WebRequest request) {
        String detail = ex.getMessage();
        HttpStatusCode status = HttpStatus.UNAUTHORIZED;
        URI type = URI.create("/api/login");

        ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
        body.setType(type);

        return ResponseEntity.of(body).build();
    }

    @ExceptionHandler(value = NotRegisteredException.class)
    public ResponseEntity<?> notRegisteredHandler(Exception ex, WebRequest request) {
        String detail = ex.getMessage();
        HttpStatusCode status = HttpStatus.FORBIDDEN;
        URI type = URI.create("/signup");

        ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
        body.setType(type);

        return ResponseEntity.of(body).build();
    }

}
