package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import jakarta.servlet.http.HttpServletResponse;

public interface CustomTokenService {
    String issueJwt(HttpServletResponse res, MemberDto memberDto);
    String reissueAccessToken(String email);
    void saveTokenOnCookie(HttpServletResponse response, TokenType type, String value);
    String issueAccessToken(MemberDto memberDto);
    String issueRefreshToken(MemberDto memberDto);
    String issueAccessAndRefreshToken(MemberDto memberDto);
}
