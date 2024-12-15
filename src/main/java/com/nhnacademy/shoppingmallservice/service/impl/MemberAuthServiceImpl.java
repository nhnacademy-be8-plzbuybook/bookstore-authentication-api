package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
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
    private final BCryptPasswordEncoder passwordEncoder;

    // 서점서버에 회원정보 요청
    @Override
    public Optional<MemberDto> getMemberByEmail(String email) {
        try {
            MemberDto memberDto = memberClient.findMemberByEmail(email);
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

            if (!passwordEncoder.matches(loginRequest.password(), memberDto.password())) {
                throw new IllegalArgumentException("wrong password");
            }
            return memberDto;
        }
        throw new NotFoundException("member not found!");
    }

}
