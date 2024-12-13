package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDTO;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/api/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDTO loginRequestDto) {
        TokenDto tokenDto = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(tokenDto);
    }


    @GetMapping("/oauth2/success")
    public String oauth2SuccessPage(Model model) {
        model.addAttribute("message", "로그인 성공");
        return "oauth2Success";
    }

    @GetMapping("/hello")
    public String hello() {
        return "여기는 인증서버 관제탑 응답하라";
    }
}
