package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import jakarta.servlet.http.HttpServletResponse;

public interface CustomTokenService {
    void issueJwt(HttpServletResponse res, MemberDto memberDto);
    void setAccessTokenCookie(HttpServletResponse response, String accessToken);
    void saveRefreshTokenOnRedis(String email, String refreshToken);
}
