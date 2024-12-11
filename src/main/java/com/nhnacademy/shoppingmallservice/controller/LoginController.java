package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.security.util.JWTUtil;
import com.nhnacademy.shoppingmallservice.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final JWTUtil jwtUtil;

    public LoginController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // JWT 토큰을 생성 (JWTUtil에서 처리)
        String token = jwtUtil.createJwt(username, "ROLE_USER", 60*60*1L);

        if (token != null) {
            return ResponseEntity.ok("로그인 성공, JWT 토큰: " + token);
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }
}