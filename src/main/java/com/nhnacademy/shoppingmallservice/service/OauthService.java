package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface OauthService {
    void redirectToOauthLoginPage(HttpServletResponse response);
    TokenDto getTokens(String code);
    String getOAuthUserEmail(String accessToken);
    String extractEmail(ResponseEntity<?> response);
}
