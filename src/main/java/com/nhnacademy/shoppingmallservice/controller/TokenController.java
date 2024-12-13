package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.AccessTokenReIssueRequestDto;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final CustomTokenServiceImpl tokenService;

    @PostMapping("/api/auth/access-token/re-issue")
    public void reIssueAccessToken(@RequestBody AccessTokenReIssueRequestDto reIssueRequest,
                                   HttpServletResponse response) {
        String reIssuedAccessToken = tokenService.reissueAccessToken(reIssueRequest);
        tokenService.setAccessTokenCookie(response, reIssuedAccessToken);
    }


//    @PostMapping("/api/auth/access-token/re-issue")
//    public void reIssueAccessToken(@RequestBody AccessTokenReIssueRequestDto reIssueRequest, // 만료된 액세스토큰 받기?
//                                   HttpServletResponse response) {
//        String expiredAccessToken = "Jwt.expired.jwt";
//        String payload = expiredAccessToken.split("\\.")[1];
//        Base64.getDecoder().decode(payload);
//
//        String reIssuedAccessToken = tokenService.reissueAccessToken(reIssueRequest);
//        tokenService.setAccessTokenCookie(response, reIssuedAccessToken);
//    }
}
