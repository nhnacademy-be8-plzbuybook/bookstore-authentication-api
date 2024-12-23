package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.common.exception.NotRegisteredException;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import com.nhnacademy.shoppingmallservice.service.impl.PaycoOauthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OauthController {
    private final MemberAuthService memberAuthService;
    private final PaycoOauthServiceImpl paycoOauthService;
    private final CustomTokenServiceImpl tokenService;

    @GetMapping("/api/auth/oauth/login")
    public void oauthLoginPage(@RequestParam("provider") String provider, HttpServletResponse response) {
        switch (provider) {
            case "payco":
                paycoOauthService.redirectToOauthLoginPage(response);
                break;
            case "kakao":
                //TODO: 카카오 oauth 로그인 구현
                break;
        }
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<?> oauthLogin(@RequestParam("code") String code, HttpServletResponse res) {
        TokenDto tokenDto = paycoOauthService.getTokens(code);
        String email = paycoOauthService.getOAuthUserEmail(tokenDto.accessToken());
//        MemberDto memberDto = new MemberDto(email, "pwd", "ROLE_MEMBER");
        Optional<MemberDto> optionalMemberDto = memberAuthService.getMemberByEmail(email);

        if (optionalMemberDto.isPresent()) {
            MemberDto memberDto = optionalMemberDto.get();
            tokenService.issueJwt(res, memberDto);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        throw new NotRegisteredException(email + "is not registered member");
//        return ResponseEntity.status(HttpStatus.FOUND).body("/signup?email=" + email);
    }
}
