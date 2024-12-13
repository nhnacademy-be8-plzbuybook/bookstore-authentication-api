package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.impl.MemberAuthServiceImpl;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final CustomTokenServiceImpl tokenService;
    private final MemberAuthServiceImpl memberAuthService;

    @GetMapping("/")
    public String hi() {
        return "hi";
    }

    @PostMapping("/api/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        MemberDto authenticatedMemberDto = memberAuthService.authenticate(loginRequest);
        tokenService.issueJwt(response, authenticatedMemberDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}