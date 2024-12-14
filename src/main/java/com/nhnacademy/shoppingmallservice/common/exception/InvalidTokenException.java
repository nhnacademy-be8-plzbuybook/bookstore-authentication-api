package com.nhnacademy.shoppingmallservice.common.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

