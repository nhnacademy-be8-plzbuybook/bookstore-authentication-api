package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.AccessTokenReIssueRequestDto;
import com.nhnacademy.shoppingmallservice.dto.AccessTokenReIssueResponseDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final CustomTokenServiceImpl tokenService;

    @PostMapping("/api/auth/access-token/re-issue")
    public ResponseEntity<AccessTokenReIssueResponseDto> reIssueAccessToken(@Valid @RequestBody AccessTokenReIssueRequestDto reIssueRequest,
                                                                            HttpServletResponse response) {

        String email = reIssueRequest.email();
        String reIssuedAccessToken = tokenService.reissueAccessToken(email);
        tokenService.saveTokenOnCookie(response, TokenType.ACCESS, reIssuedAccessToken);

        AccessTokenReIssueResponseDto body = new AccessTokenReIssueResponseDto(reIssuedAccessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }


    // 만료된 액세스토큰 자체를 받아 디코딩해서 페이로드 값을 복사
//    @PostMapping("/api/auth/access-token/re-issue")
//    public void reIssueAccessToken(@RequestBody AccessTokenReIssueRequestDto reIssueRequest,
//                                   HttpServletRequest request,
//                                   HttpServletResponse response) {
//        String originAddress = "";
//        tokenService.checkUserAddress(originAddress, request);
//        String expiredAccessToken = "Jwt.expired.jwt";
//        String payload = expiredAccessToken.split("\\.")[1];
//        Base64.getDecoder().decode(payload);
//
//        String reIssuedAccessToken = tokenService.reissueAccessToken(reIssueRequest);
////        tokenService.setAccessTokenCookie(response, reIssuedAccessToken);
//    }
}
