package com.nhnacademy.shoppingmallservice.util;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {
    private JwtProvider jwtProvider;

    @Mock
    private JwtProperties jwtProperties;

//    private final String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVsdfsdfsdfwuY29tIiwiZXhwIjoxNzM0NDgyNjkwfQ.H-A849hldL37X2CbQp7AgIRjaSRsXSpK4aZ3xifdqLw";

    @BeforeEach
    void setup() {
        lenient().when(jwtProperties.getAccessExpirationTime()).thenReturn(900000L);
        lenient().when(jwtProperties.getRefreshExpirationTime()).thenReturn(604800000L);
        lenient().when(jwtProperties.getSecret()).thenReturn("Ny0pm2CWIAST07ElsTAVZgCqJKJd2bE9lpKyewuthisisfakeSecretWIAST07ElsTAVZgCqJKJd2bE9lpKyewuOhyyKoBApt1");
        lenient().when(jwtProperties.getHeaderString()).thenReturn("Authorization");
        lenient().when(jwtProperties.getTokenPrefix()).thenReturn("Bearer");

        jwtProvider = new JwtProvider(jwtProperties);
    }


    @Test
    @DisplayName("accessToken 생성 테스트 - 호출 검증")
    void generateAccessToken() {
        // given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");

        // when
        String token = jwtProvider.generateAccessToken(memberDto);

        // then
        assertNotNull(token);
        Claims claims = jwtProvider.parseToken(token);
        assertEquals("test@email.com", claims.getSubject());
        assertEquals("ROLE_MEMBER", claims.get("role"));
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
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");

        //when
        String token = jwtProvider.generateRefreshToken(memberDto);

        //then
        assertNotNull(token, "토큰이 null이 아니어야 합니다.");
        Claims claims = jwtProvider.parseToken(token);
        assertEquals("test@email.com", claims.getSubject(), "토큰의 subject가 예상 값과 다릅니다.");
        assertEquals("ROLE_MEMBER", claims.get("role"), "토큰의 role 클레임이 예상 값과 다릅니다.");
        assertEquals("WITHDRAWAL", claims.get("memberStateName"), "토큰의 memberStateName 클레임이 예상 값과 다릅니다.");
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
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
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
        //given
        when(jwtProperties.getAccessExpirationTime()).thenReturn(1L);
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
        String expiredToken = jwtProvider.generateAccessToken(memberDto);

        //when & then
        ExpiredJwtException e = assertThrows(ExpiredJwtException.class, () -> jwtProvider.parseToken(expiredToken));

    }

    @DisplayName("토큰 검증")
    @Test
    void isValidToken() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
        String token = jwtProvider.generateRefreshToken(memberDto);

        //when
        jwtProvider.validateToken(token);
    }

    @DisplayName("토큰 검증 - 만료된")
    @Test
    void validateToken_expired() {
        //given
        when(jwtProperties.getAccessExpirationTime()).thenReturn(1L);
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
        String expiredToken = jwtProvider.generateAccessToken(memberDto);

        //when & then
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(expiredToken));

        assertEquals("Expired JWT token", e.getMessage());
    }

    @DisplayName("토큰 검증 - 잘못된 시그니처")
    @Test
    void isValidToken_invalid_signature() {
        //given
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
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
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");
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

        assertEquals("Invalid token", e.getMessage());

    }


}