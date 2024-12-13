package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import com.nhnacademy.shoppingmallservice.service.impl.PaycoOauthServiceImpl;
import com.nhnacademy.shoppingmallservice.webClient.FrontClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OauthController {

    private final PaycoOauthServiceImpl paycoOauthService;
    private final FrontClient frontClient;
    private final CustomTokenServiceImpl tokenService;

    @GetMapping("/api/oauth/login") //?provider=payco로 변경
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
    public void oauthLogin(@RequestParam("code") String code, HttpServletResponse res) {
        TokenDto tokenDto = paycoOauthService.getTokens(code);
        String email = paycoOauthService.getOAuthUserEmail(tokenDto.accessToken());

        MemberDto memberDto = new MemberDto(email, "pwd", "ROLE_MEMBER");
//        MemberDto memberDto = memberAuthService.getMemberByEmail(email);

        if (memberDto != null) {
            tokenService.issueJwt(res, memberDto);
        } else {
            frontClient.redirectToSignupPage(email);
        }
    }
}
