package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.OauthLoginResponseDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import com.nhnacademy.shoppingmallservice.service.impl.PaycoOauthServiceImpl;
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
    public ResponseEntity<OauthLoginResponseDto> oauthLogin(@RequestParam("code") String code) {
        TokenDto tokenDto = paycoOauthService.getTokens(code);
        String email = paycoOauthService.getOAuthUserEmail(tokenDto.accessToken());
        Optional<MemberDto> optionalMemberDto = memberAuthService.getMemberByEmail(email);

        // 등록된 회원이 있으면 액세스토큰 발급하고 액세스토큰 응답
        if (optionalMemberDto.isPresent()) {
            MemberDto memberDto = optionalMemberDto.get();
            String accessToken = tokenService.issueAccessAndRefreshToken(memberDto);
            OauthLoginResponseDto oauthLoginResponse = new OauthLoginResponseDto(true, email, accessToken);

            return ResponseEntity.status(HttpStatus.OK).body(oauthLoginResponse);
        }

        // 등록된 회원이 없을때 이메일로 응답
        OauthLoginResponseDto oauthLoginResponse = new OauthLoginResponseDto(false, email, null);
        return ResponseEntity.status(HttpStatus.OK).body(oauthLoginResponse);
    }
}
