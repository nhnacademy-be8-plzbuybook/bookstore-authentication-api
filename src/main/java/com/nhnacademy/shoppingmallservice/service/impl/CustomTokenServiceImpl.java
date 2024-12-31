package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.LoginFailException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.CookieService;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.lang.model.element.NestingKind;
import java.lang.reflect.Member;

@RequiredArgsConstructor
@Service
public class CustomTokenServiceImpl implements CustomTokenService {
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final RedisService redisService;
    private final MemberClient memberClient;

    public String issueJwt(HttpServletResponse res, MemberDto memberDto) {
        validateMemberState(memberDto); // 추가
        String accessToken = jwtProvider.generateAccessToken(memberDto);
        String refreshToken = jwtProvider.generateRefreshToken(memberDto);
//        saveTokenOnCookie(res, TokenType.ACCESS, accessToken);
        redisService.saveValueOnRedis(jwtProvider.getRefreshTokenKey(memberDto.email()), refreshToken, jwtProvider.getRefreshExpirationTime());
        return accessToken;
    }

    private void validateMemberState(MemberDto memberDto) {
        if ("WITHDRAWAL".equals(memberDto.memberStateName())) {
            throw new LoginFailException("이미 탈퇴한 회원입니다.");
        }
    }

    public String issueAccessAndRefreshToken(MemberDto memberDto) {
        String accessToken = issueAccessToken(memberDto);
        String refreshToken = issueRefreshToken(memberDto);

        redisService.saveValueOnRedis(jwtProvider.getRefreshTokenKey(memberDto.email()), refreshToken, jwtProvider.getRefreshExpirationTime());
        return accessToken;
    }

    // Refresh Token 검증 및 Access Token 재발급
    public String reissueAccessToken(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("parameter cant not be blank");
        }

        MemberDto memberDto = getMemberByEmail(email);

        if (memberDto == null) {
            throw new LoginFailException("회원 정보가 존재하지 않습니다.");
        }

        validateMemberState(memberDto);

        String refreshTokenKey = jwtProvider.getRefreshTokenKey(email);
        String refreshToken = (String) redisService.getValueFromRedis(refreshTokenKey);

        // 리프레쉬 토큰 검증
        jwtProvider.validateToken(refreshToken);

        Claims claims = jwtProvider.parseToken(refreshToken);
        String role = (String) claims.get("role");
        String memberStateName = (String) claims.get("memberStateName");

        if (role == null || memberStateName == null) {
            throw new LoginFailException("토큰에 필수 클레임 값이 누락되었습니다.");
        }

        memberDto = new MemberDto(email, null, role, memberStateName);
        return jwtProvider.generateAccessToken(memberDto);
    }



    private MemberDto getMemberByEmail(String email) {
        try {
            return memberClient.findMemberByEmail(email); // MemberClient를 통해 회원 정보 가져오기
        } catch (FeignException.NotFound e) {
            throw new LoginFailException("회원 정보가 존재하지 않습니다.");
        }
    }

    @Override
    public void saveTokenOnCookie(HttpServletResponse response, TokenType type, String token) {
        jwtProvider.validateToken(token);

        String key = (type == TokenType.ACCESS) ? "accessToken" : "refreshToken";
        long expiry = (type == TokenType.ACCESS) ? jwtProvider.getAccessExpirationTime() : jwtProvider.getRefreshExpirationTime();
        cookieService.saveOnCookie(response, key, token, (int) expiry / 1000);
    }

    @Override
    public String issueAccessToken(MemberDto memberDto) {
        return jwtProvider.generateAccessToken(memberDto);
    }

    @Override
    public String issueRefreshToken(MemberDto memberDto) {
        return jwtProvider.generateRefreshToken(memberDto);
    }

//    public void checkUserAddress(String originAddress, HttpServletRequest request) {
//        String userAddress = getRemoteAddress(request);
//
//        if (userAddress.equals(originAddress)) {
//            //TODO: 예외처리 or 로그인 페이지 요청
//        }
//    }
//
//    public String getRemoteAddress(HttpServletRequest request) {
//        return (request.getHeader("X-FORWARDED-FOR") != null) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
//    }

}