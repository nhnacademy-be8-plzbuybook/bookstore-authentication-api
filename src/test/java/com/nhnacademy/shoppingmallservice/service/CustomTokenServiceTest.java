package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomTokenServiceTest {
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CookieService cookieService;
    @Mock
    private RedisService redisService;
    @InjectMocks
    private CustomTokenServiceImpl customTokenService;


    @Test
    void issueJwt() {
        // given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        MemberDto memberDto = mock(MemberDto.class);

    }

    @Test
    void reIssueAccessToken() {
        //given
        String email = "test@email.com";
        String refreshTokenKey = "refresh_token:" + email;
        String refreshToken = "refreshToken";
        Claims mockClaims = mock(Claims.class);

        when(jwtProvider.getRefreshTokenKey(email)).thenReturn(refreshTokenKey);
        when(redisService.getValueFromRedis(refreshTokenKey)).thenReturn(refreshToken);
        doNothing().when(jwtProvider).validateToken(refreshToken);
        when(jwtProvider.parseToken(refreshToken)).thenReturn(mockClaims);
        when(mockClaims.get("role")).thenReturn("ROLE_MEMBER");
        when(jwtProvider.generateAccessToken(any(MemberDto.class))).thenReturn("accessToken");

        //when
        customTokenService.reissueAccessToken(email);

        //then
        verify(jwtProvider).getRefreshTokenKey(email);
        verify(redisService).getValueFromRedis(refreshTokenKey);
        verify(jwtProvider).validateToken(refreshToken);
        verify(jwtProvider).parseToken(refreshToken);
        verify(mockClaims).get("role");
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
        //given
        String email = "test@email.com";
        String refreshTokenKey = "refresh_token:" + email;
        String refreshToken = "refreshToken";

        when(jwtProvider.getRefreshTokenKey(email)).thenReturn(refreshTokenKey);
        when(redisService.getValueFromRedis(refreshTokenKey)).thenReturn(refreshToken);
        doThrow(InvalidTokenException.class).when(jwtProvider).validateToken(refreshToken);

        //when
        assertThrows(InvalidTokenException.class, () -> customTokenService.reissueAccessToken(email));

        //then
        verify(jwtProvider).getRefreshTokenKey(email);
        verify(redisService).getValueFromRedis(refreshTokenKey);
        verify(jwtProvider, never()).parseToken(refreshToken);
        verify(jwtProvider, never()).generateAccessToken(any(MemberDto.class));
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