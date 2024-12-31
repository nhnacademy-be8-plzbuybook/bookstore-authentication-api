package com.nhnacademy.shoppingmallservice.common.exception;

public class LoginFailException extends RuntimeException {
    public LoginFailException(String message) {
        super(message);
    }
}

