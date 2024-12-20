package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.CookieService;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomTokenServiceImpl implements CustomTokenService {
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final RedisService redisService;

    public String issueJwt(HttpServletResponse res, MemberDto memberDto) {
        String accessToken = jwtProvider.generateAccessToken(memberDto);
        String refreshToken = jwtProvider.generateRefreshToken(memberDto);
//        saveTokenOnCookie(res, TokenType.ACCESS, accessToken);
        redisService.saveValueOnRedis(jwtProvider.getRefreshTokenKey(memberDto.email()), refreshToken, jwtProvider.getRefreshExpirationTime());
        return accessToken;
    }

    // Refresh Token 검증 및 Access Token 재발급
    public String reissueAccessToken(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("parameter cant not be blank");
        }
        String refreshTokenKey = jwtProvider.getRefreshTokenKey(email);
        String refreshToken = (String) redisService.getValueFromRedis(refreshTokenKey);

        // 리프레쉬 토큰 검증
        jwtProvider.validateToken(refreshToken);

        Claims claims = jwtProvider.parseToken(refreshToken);
        String role = (String) claims.get("role");
        MemberDto memberDto = new MemberDto(email, null, role);
        return jwtProvider.generateAccessToken(memberDto);
    }

    @Override
    public void saveTokenOnCookie(HttpServletResponse response, TokenType type, String token) {
        jwtProvider.validateToken(token);

        String key = (type == TokenType.ACCESS) ? "accessToken" : "refreshToken";
        long expiry = (type == TokenType.ACCESS) ? jwtProvider.getAccessExpirationTime() : jwtProvider.getRefreshExpirationTime();
        cookieService.saveOnCookie(response, key, token, (int) expiry / 1000);
    }

//    public void checkUserAddress(String originAddress, HttpServletRequest request) {
//        String userAddress = getRemoteAddress(request);
//
//        if (userAddress.equals(originAddress)) {
//            //TODO: 예외처리 or 로그인 페이지 요청
//        }
//    }
//
//    public String getRemoteAddress(HttpServletRequest request) {
//        return (request.getHeader("X-FORWARDED-FOR") != null) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
//    }
}