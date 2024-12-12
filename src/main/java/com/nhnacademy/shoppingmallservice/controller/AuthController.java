package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
//    private final OAuth2AuthorizeService oAuth2AuthorizeService;
//    private final TokenService tokenService;
//
//    @GetMapping("/login/payco")
//    public void Login(HttpServletResponse response) {
//        oAuth2AuthorizeService.redirectOauth2LoginPage(response);
//    }
//
//    @GetMapping("/oauth/callback")
//    public void getCode(@RequestParam("code") String code) {
//        System.out.println(code);
//        TokenDto token = oAuth2AuthorizeService.getTokens(code);
//        oAuth2AuthorizeService.getOAuthUserEmail(token.accessToken());
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody MemberDto memberDto, HttpServletResponse response) {
//        // Access Token 생성 및 쿠키 저장
//        String accessToken = tokenService.generateAccessToken(memberDto);
//        tokenService.setAccessTokenCookie(response, accessToken);
//
//        // Refresh Token Redis 저장
//        tokenService.saveRefreshTokenOnRedis(memberDto);
//
//        return ResponseEntity.ok("Login successful");
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh(@RequestBody MemberDto memberDto, @RequestBody String refreshToken) {
//        // Refresh Token 검증 및 Access Token 재발급
//        String newAccessToken = tokenService.reissueAccessToken(memberDto.email(), refreshToken);
//
//        return ResponseEntity.ok(newAccessToken);
//    }


    @GetMapping("/hello")
    public String hello() {
        return "여기는 인증서버 관제탑 응답하라";
    }
}
