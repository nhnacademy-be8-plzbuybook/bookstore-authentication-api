package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberAuthServiceImpl implements MemberAuthService {
    private final MemberClient memberClient;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서점서버에 회원정보 요청
    @Override
    public MemberDto getMemberByEmail(String email) {
        try {
            return memberClient.findMemberByEmail(email);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    // 회원인증(로그인)
    @Override
    public MemberDto authenticate(LoginRequestDto loginRequest) {
        String email = loginRequest.email();
        MemberDto memberDto = getMemberByEmail(email);

        if (!passwordEncoder.matches(loginRequest.password(), memberDto.password())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        return memberDto;
    }

}
