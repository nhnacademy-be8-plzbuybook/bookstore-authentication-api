package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class CustomTokenServiceImpl implements CustomTokenService {
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    public void issueJwt(HttpServletResponse res, MemberDto memberDto) {
        String accessToken = jwtUtil.generateAccessToken(memberDto);
        String refreshToken = jwtUtil.generateRefreshToken(memberDto);
        setAccessTokenCookie(res, accessToken);
        saveRefreshTokenOnRedis(memberDto.email(), refreshToken);
    }

    // AccessToken 을 쿠키에 저장
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) (jwtUtil.getAccessTokenValidity() / 1000));

        response.addCookie(cookie);
    }

    // Refresh Token 을 Redis 에 저장
    public void saveRefreshTokenOnRedis(String email, String refreshToken) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );
    }

    // Refresh Token 검증 및 Access Token 재발급
    public String reissueAccessToken(String email, String refreshToken) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + email;
        String storedRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token 과 맞지 않습니다");
        }

        MemberDto memberDto = new MemberDto(UUID.randomUUID().toString(), email, null);
        return jwtUtil.generateAccessToken(memberDto);
    }
}
