package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDTO;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.entity.Users;
import com.nhnacademy.shoppingmallservice.repository.MemberRepository;
import com.nhnacademy.shoppingmallservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TokenDto authenticate(LoginRequestDTO loginRequestDto) {

        Users user = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        MemberDto memberDto = new MemberDto(UUID.randomUUID().toString(), user.getEmail(), user.getRole());

        String accessToken = jwtUtil.generateAccessToken(memberDto);
        String refreshToken = jwtUtil.generateRefreshToken(memberDto);

        return new TokenDto(
                "Bearer",
                accessToken,
                refreshToken,
                String.valueOf(jwtUtil.getExpirationTime())
        );
    }
}
