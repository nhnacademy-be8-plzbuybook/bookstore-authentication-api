package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberAuthServiceImpl implements MemberAuthService {
    private final MemberClient memberClient;

    // 서점서버에 회원정보 요청
    @Override
    public Optional<MemberDto> getMemberByEmail(String email) {
        try {
            // 멤버 받았다 치고
            MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");
//            MemberDto memberDto = memberClient.findMemberByEmail(email);
            return Optional.of(memberDto);
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    // 회원인증(로그인)
    @Override
    public MemberDto authenticate(LoginRequestDto loginRequest) {
        Optional<MemberDto> optionalMemberDto = getMemberByEmail(loginRequest.email());

        if (optionalMemberDto.isPresent()) {
            MemberDto memberDto = optionalMemberDto.get();

            if (loginRequest.password().equals(memberDto.password())) {
                return memberDto;
            }
        }
        throw new UnAuthorizedException("wrong Id or Password");
    }

}
