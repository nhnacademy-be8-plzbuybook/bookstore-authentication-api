package com.nhnacademy.shoppingmallservice.service;

public interface AccountService {
    void sendVerificationCode(String userId);
    boolean verifyCode(String userId, String inputCode);
    String getRoleFromToken(String token);
    String getEmailFromToken(String token);
}
