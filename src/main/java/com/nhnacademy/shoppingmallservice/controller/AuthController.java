package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.LoginResponseDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.UpdateLastLoginRequestDto;
import com.nhnacademy.shoppingmallservice.service.AccountService;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final CustomTokenService tokenService;
    private final MemberAuthService memberAuthService;
    private final AccountService accountService;
    private final MemberClient memberClient;

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

        try {
            UpdateLastLoginRequestDto updateLastLoginRequestDto = new UpdateLastLoginRequestDto(
                    memberDto.email(),
                    LocalDateTime.now()
            );
            memberClient.updateLastLogin(updateLastLoginRequestDto);
        } catch (Exception e) {
            log.error("마지막 로그인 업데이트 실패: {}", e.getMessage());
        }

        LoginResponseDto successResponse = new LoginResponseDto(accessToken, memberDto.memberStateName(), null, memberDto.role());
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);

    }


    @GetMapping("/api/auth/role")
    public ResponseEntity<String> getRoleFromToken(@RequestHeader("Authorization") String authorizationHeader){
        try{
            String token = authorizationHeader.substring(7);
            String role = accountService.getRoleFromToken(token);
            return ResponseEntity.ok(role);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



//    @PostMapping("/api/auth/login")
//    public ResponseEntity<MemberDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
//        MemberDto authenticatedMemberDto = memberAuthService.authenticate(loginRequest);
//        return ResponseEntity.status(HttpStatus.OK).body(authenticatedMemberDto);
//    }
}
