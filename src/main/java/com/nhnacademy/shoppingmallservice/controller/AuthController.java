package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.LoginResponseDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
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

    private final CustomTokenService tokenService;
    private final MemberAuthService memberAuthService;

    @GetMapping("/")
    public String hi() {
        return "hi";
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        MemberDto memberDto = memberAuthService.authenticate(loginRequest);

        if ("DORMANT".equals(memberDto.memberStateName())) {
            LoginResponseDto dormantResponse = new LoginResponseDto(null, memberDto.memberStateName(), "/auth/verify-code", null);

            return ResponseEntity.status(HttpStatus.OK).body(dormantResponse);
        }

        String accessToken = tokenService.issueAccessAndRefreshToken(memberDto);
        LoginResponseDto successResponse = new LoginResponseDto(accessToken, memberDto.memberStateName(), null, memberDto.role());
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }



//    @PostMapping("/api/auth/login")
//    public ResponseEntity<MemberDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
//        MemberDto authenticatedMemberDto = memberAuthService.authenticate(loginRequest);
//        return ResponseEntity.status(HttpStatus.OK).body(authenticatedMemberDto);
//    }
}