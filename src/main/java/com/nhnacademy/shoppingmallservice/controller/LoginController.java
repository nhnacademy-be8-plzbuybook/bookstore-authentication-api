package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.security.util.JwtUtil;
import com.nhnacademy.shoppingmallservice.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto, HttpServletResponse response) {
        // Access Token 생성
        String accessToken = jwtUtil.generateAccessToken(memberDto);

        // Access Token을 쿠키에 저장
        tokenService.setAccessTokenCookie(response, accessToken);

        // Refresh Token 생성 및 Redis 저장
        String refreshToken = jwtUtil.generateRefreshToken(memberDto);
        tokenService.saveRefreshTokenOnRedis(memberDto.email(), refreshToken);

        return ResponseEntity.ok("로그인 성공");
    }
}