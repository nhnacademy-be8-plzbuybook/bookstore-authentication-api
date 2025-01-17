package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.exception.LoginFailException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.CookieService;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomTokenServiceImplTest {
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CookieService cookieService;
    @Mock
    private RedisService redisService;
    @Mock
    MemberClient memberClient;
    @InjectMocks
    private CustomTokenServiceImpl customTokenService;


    @Test
    void issueJwt() {
        // given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        MemberDto memberDto = mock(MemberDto.class);
        when(memberDto.memberStateName()).thenReturn("WITHDRAWAL");
        Exception e = assertThrows(LoginFailException.class, () -> customTokenService.issueJwt(mockResponse, memberDto));
        assertEquals("이미 탈퇴한 회원입니다.", e.getMessage());

    }

    @Test
    void reIssueAccessToken() {
        // given
        String email = "test@email.com";
        String refreshTokenKey = "refresh_token:" + email;
        String refreshToken = "refreshToken";
        Claims mockClaims = mock(Claims.class);
        MemberDto memberDto = new MemberDto(email, null, "ROLE_MEMBER", "ACTIVE");

        // Mock 설정
        when(jwtProvider.getRefreshTokenKey(email)).thenReturn(refreshTokenKey);
        when(redisService.getValueFromRedis(refreshTokenKey)).thenReturn(refreshToken);
        doNothing().when(jwtProvider).validateToken(refreshToken);
        when(jwtProvider.parseToken(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.get("role")).thenReturn("ROLE_MEMBER");
        when(mockClaims.get("memberStateName")).thenReturn("ACTIVE");
        when(memberClient.findMemberByEmail(email)).thenReturn(memberDto);
        when(jwtProvider.generateAccessToken(any(MemberDto.class))).thenReturn("accessToken");

        // when
        String accessToken = customTokenService.reissueAccessToken(email);

        // then
        assertNotNull(accessToken, "Access Token이 null이 아닙니다.");
        assertEquals("accessToken", accessToken, "Access Token 값이 예상과 일치합니다.");
        verify(jwtProvider).getRefreshTokenKey(email);
        verify(redisService).getValueFromRedis(refreshTokenKey);
        verify(jwtProvider).validateToken(refreshToken);
        verify(jwtProvider).parseToken(refreshToken);
        verify(mockClaims).get("role");
        verify(mockClaims).get("memberStateName");
        verify(memberClient).findMemberByEmail(email);
        verify(jwtProvider).generateAccessToken(any(MemberDto.class));
    }

    @Test
    void reIssueAccessToken_blank_email() {
        //given
        String email = " ";

        //when
        Exception e = assertThrows(IllegalArgumentException.class , () -> customTokenService.reissueAccessToken(email));

        //then
        assertEquals("parameter cant not be blank", e.getMessage());
    }

    @Test
    void reIssueAccessToken_invalid_refreshToken() {
        // given
        String email = "test@email.com";
        String refreshTokenKey = "refresh_token:" + email;
        String refreshToken = "refreshToken";
        MemberDto memberDto = new MemberDto(email, null, "ROLE_MEMBER", "ACTIVE");

        when(jwtProvider.getRefreshTokenKey(email)).thenReturn(refreshTokenKey);
        when(redisService.getValueFromRedis(refreshTokenKey)).thenReturn(refreshToken);
        when(memberClient.findMemberByEmail(email)).thenReturn(memberDto); // Mock 설정 추가
        doThrow(new InvalidTokenException("Invalid token", null)).when(jwtProvider).validateToken(refreshToken);

        // when
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> customTokenService.reissueAccessToken(email));

        // then
        assertEquals("Invalid token", exception.getMessage()); // 메시지 검증
        verify(jwtProvider).getRefreshTokenKey(email);
        verify(redisService).getValueFromRedis(refreshTokenKey);
        verify(jwtProvider).validateToken(refreshToken);
        verify(jwtProvider, never()).parseToken(refreshToken); // 파싱이 호출되지 않음을 검증
        verify(jwtProvider, never()).generateAccessToken(any(MemberDto.class)); // 토큰 생성이 호출되지 않음을 검증
    }



    @Test
    void saveTokenOnCookie_invalid_token() {
        //given
        String invalidToken = "invalidToken";
        doThrow(InvalidTokenException.class).when(jwtProvider).validateToken(invalidToken);

        //when
        assertThrows(InvalidTokenException.class, () -> customTokenService.saveTokenOnCookie(mock(HttpServletResponse.class), TokenType.ACCESS, invalidToken));

        //then
        verify(jwtProvider, never()).getAccessExpirationTime();
        verify(jwtProvider, never()).getRefreshExpirationTime();
        verify(cookieService, never()).saveOnCookie(any(), anyString(), anyString(), anyInt());
    }

    @Test
    void saveTokenOnCookie_accessToken() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        String accessToken = "accessToken";
        long expiry = 1000000L;

        when(jwtProvider.getAccessExpirationTime()).thenReturn(expiry);

        //when
        customTokenService.saveTokenOnCookie(response, TokenType.ACCESS, accessToken);

        // then
        verify(jwtProvider).validateToken(accessToken);
        verify(jwtProvider).getAccessExpirationTime();
        verify(jwtProvider, never()).getRefreshExpirationTime();

        int calculatedExpiry = (int) expiry / 1000;
        verify(cookieService).saveOnCookie(response, "accessToken", accessToken, calculatedExpiry);
    }

    @Test
    void saveTokenOnCookie_refreshToken() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        String refreshToken = "refreshToken";
        long expiry = 1000000L;

        when(jwtProvider.getRefreshExpirationTime()).thenReturn(expiry);

        //when
        customTokenService.saveTokenOnCookie(response, TokenType.REFRESH, refreshToken);

        // then
        verify(jwtProvider).validateToken(refreshToken);
        verify(jwtProvider).getRefreshExpirationTime();
        verify(jwtProvider, never()).getAccessExpirationTime();

        int calculatedExpiry = (int) expiry / 1000;
        verify(cookieService).saveOnCookie(response, "refreshToken", refreshToken, calculatedExpiry);
    }

}