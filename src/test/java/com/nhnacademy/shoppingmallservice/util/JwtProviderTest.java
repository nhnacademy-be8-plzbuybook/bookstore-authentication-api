package com.nhnacademy.shoppingmallservice.util;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(value = "secret")
@EnableConfigurationProperties(JwtProperties.class)
class JwtProviderTest {
    @Autowired
    private JwtProvider jwtProvider;

    @SpyBean
    private JwtProperties jwtProperties;

    private final String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLCJpYXQiOjE3MzQ0ODE3OTAsInN1YiI6InRlc3RAZW1haWwuY29tIiwiZXhwIjoxNzM0NDgyNjkwfQ.H-A849hldL37X2CbQp7AgIRjaSRsXSpK4aZ3xifdqLw";

    @Test
    @DisplayName("accessToken 생성 테스트 - 호출 검증")
    void generateAccessToken() {
        // given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");

        // when
        String token = jwtProvider.generateAccessToken(memberDto);

        // then
        assertNotNull(token);
        Claims claims = jwtProvider.parseToken(token);
        assertEquals("test@email.com", claims.getSubject());
        assertEquals("ROLE_MEMBER", claims.get("role"));
        verify(jwtProperties, times(1)).getAccessExpirationTime();
    }

    @DisplayName("accessToken 생성 테스트 - null 인자")
    @Test
    void generateAccessToken_null_parameter() {
        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> jwtProvider.generateAccessToken(null));
        assertEquals("invalid MemberDto", e.getMessage());
    }

    @DisplayName("refreshToken 생성 테스트")
    @Test
    void generateRefreshToken() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");

        //when
        String token = jwtProvider.generateRefreshToken(memberDto);

        //then
        assertNotNull(token);
        Claims claims = jwtProvider.parseToken(token);
        assertEquals("test@email.com", claims.getSubject());
        assertEquals("ROLE_MEMBER", claims.get("role"));
        verify(jwtProperties, times(1)).getRefreshExpirationTime();
    }

    @DisplayName("refreshToken 생성 테스트 - null 인자")
    @Test
    void generateRefreshToken_null_parameter() {
        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> jwtProvider.generateRefreshToken(null));
        assertEquals("invalid MemberDto", e.getMessage());
    }


    @Test
    void getRefreshTokenKey() {
        String email = "test@email.com";
        String key = jwtProvider.getRefreshTokenKey(email);

        assertNotNull(key);
        assertEquals("refresh_token:" + email, key);
    }

    @DisplayName("토큰 파싱 테스트")
    @Test
    void parseToken() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");
        String token = jwtProvider.generateRefreshToken(memberDto);

        //when
        Claims claims = jwtProvider.parseToken(token);

        //then
        assertEquals("test@email.com", claims.getSubject());
        assertEquals("ROLE_MEMBER", claims.get("role"));
    }

    @DisplayName("토큰 파싱 테스트 - 만료된")
    @Test
    void parseToken_expired() {
        //when & then
        ExpiredJwtException e = assertThrows(ExpiredJwtException.class, () -> jwtProvider.parseToken(expiredToken));

    }

    @DisplayName("토큰 검증")
    @Test
    void isValidToken() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");
        String token = jwtProvider.generateRefreshToken(memberDto);

        //when
        jwtProvider.validateToken(token);
    }

    @DisplayName("토큰 검증 - 만료된")
    @Test
    void validateToken_expired() {
        //when & then
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(expiredToken));

        assertEquals("Expired JWT token", e.getMessage());
    }

    @DisplayName("토큰 검증 - 잘못된 시그니처")
    @Test
    void isValidToken_invalid_signature() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");
        String token = jwtProvider.generateRefreshToken(memberDto);
        String invalidSignatureToken = token + "wrongSignature";
        //when & then
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(invalidSignatureToken));

        assertEquals("Invalid JWT signature", e.getMessage());
    }

    @DisplayName("토큰 검증 - 변조된 토큰")
    @Test
    void isValidToken_malformed() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");
        String token = jwtProvider.generateRefreshToken(memberDto);
        String[] tokenContent = token.split("\\.");
        String header = tokenContent[0];
        String payload = tokenContent[1] + "malformed";
        String signature = tokenContent[2];
        String malformedToken = header + payload + signature;

        //when & then
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(malformedToken));

        assertEquals("Malformed JWT token", e.getMessage());
    }


    @DisplayName("토큰 검증 - null")
    @Test
    void isValidToken_null() {
        //when & then
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(null));

        assertEquals("Invalid JWT token", e.getMessage());

    }

}