package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.LoginFailException;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberAuthServiceImpl implements MemberAuthService {
    private final MemberClient memberClient;
    private final PasswordEncoder passwordEncoder;

    // 서점서버에 회원정보 요청
    @Override
    public Optional<MemberDto> getMemberByEmail(String email) {
        try {
            MemberDto memberDto = memberClient.findMemberByEmail(email);
            return Optional.of(memberDto);
        } catch (FeignException.NotFound e) {
            // 회원이 없는 경우
            return Optional.empty();
        } catch (FeignException e) {
            // 기타 Feign 예외 발생 시
            throw new RuntimeException("서버 오류로 인해 회원 정보를 가져올 수 없습니다.");
        }
    }

    // 회원인증(로그인)
    @Override
    public MemberDto authenticate(LoginRequestDto loginRequest) {
        // 이메일로 회원 정보 조회
        MemberDto memberDto = getMemberByEmail(loginRequest.email())
                .orElseThrow(() -> new UnAuthorizedException("회원 정보가 존재하지 않습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password(), memberDto.password())) {
            throw new UnAuthorizedException("잘못된 아이디 또는 비밀번호입니다.");
        }

        // 상태 검증
//        validateMemberState(memberDto);

        return memberDto;
    }

    private void validateMemberState(MemberDto memberDto) {
        if ("WITHDRAWAL".equals(memberDto.memberStateName())) {
            throw new LoginFailException("이미 탈퇴한 회원입니다.");
        }
    }
}