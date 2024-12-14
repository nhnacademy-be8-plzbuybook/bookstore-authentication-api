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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {
    @Mock
    private JwtProperties mockJwtProperties;
    private JwtProvider jwtProvider;

    @BeforeEach
    void setup() {
        String secret = "Ny0pm2CWIAST07ElsTAVZgCqJKJd2bE9lpKyewuOhyyKoBApt1Ny0pm2CWIAST07ElsTAVZgCqJKJd2bE9lpKyewuOhyyKoBApt1";
        when(mockJwtProperties.getSecret()).thenReturn(secret);
        jwtProvider = new JwtProvider(mockJwtProperties);
    }

    @DisplayName("accessToken 생성 테스트")
    @Test
    void generateAccessToken() {
        String testEmail = "test@email.com";
        String testRole = "ROLE_MEMBER";
        MemberDto memberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(900000L);

        String accessToken = jwtProvider.generateAccessToken(memberDto);

        assertNotNull(accessToken);
        verify(mockJwtProperties, times(1)).getAccessExpirationTime();

        String payload = new String(Base64.getDecoder().decode(accessToken.split("\\.")[1]), StandardCharsets.UTF_8);
        assertTrue(payload.contains(testEmail));
        assertTrue(payload.contains(testRole));
    }

    @DisplayName("accessToken 생성 테스트 - 잘못된 인자")
    @Test
    void generateAccessToken_wrong_param() {
        String testEmail = "";
        String testRole = "ROLE_MEMBER";
        MemberDto memberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(900000L);

        assertThrows(IllegalArgumentException.class, () -> jwtProvider.generateAccessToken(memberDto));
    }

    @DisplayName("refreshToken 생성 테스트")
    @Test
    void generateRefreshToken() {
        String testEmail = "test@email.com";
        String testRole = "ROLE_MEMBER";
        MemberDto memberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getRefreshExpirationTime()).thenReturn(604800000L);

        String refreshToken = jwtProvider.generateRefreshToken(memberDto);

        assertNotNull(refreshToken);
        verify(mockJwtProperties, times(1)).getRefreshExpirationTime();

        String payload = new String(Base64.getDecoder().decode(refreshToken.split("\\.")[1]), StandardCharsets.UTF_8);
        assertTrue(payload.contains(testEmail));
        assertTrue(payload.contains(testRole));
    }


    @Test
    void getRefreshTokenKey() {
        String testEmail = "test@email.com";

        String key = jwtProvider.getRefreshTokenKey(testEmail);

        assertEquals("refresh_token:" + testEmail, key);
    }

//    @DisplayName("Redis에서 refreshToken 조회")
//    @Test
//    void fetchRefreshToken() {
//        String testEmail = "test@email.com";
//        String refreshToken = "refreshToken";
//        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
//        when(mockRedisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(valueOperations.get(anyString())).thenReturn(refreshToken);
//
//        String fetchedRefreshToken = jwtProvider.fetchRefreshToken(testEmail);
//
//        assertNotNull(fetchedRefreshToken);
//        assertEquals(refreshToken, fetchedRefreshToken);
//        verify(mockRedisTemplate, times(1)).opsForValue();
//        verify(valueOperations, times(1)).get(anyString());
//    }
//
//    @DisplayName("Redis에서 refreshToken 조회 - 존재하지 않는 refreshToken")
//    @Test
//    void fetchRefreshToken_not_exist() {
//        String testEmail = "notExist@email.com";
//        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
//        when(mockRedisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(valueOperations.get(anyString())).thenReturn(null);
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> jwtProvider.fetchRefreshToken(testEmail));
//        assertEquals("token not exist!", exception.getMessage());
//        verify(mockRedisTemplate, times(1)).opsForValue();
//        verify(valueOperations, times(1)).get(anyString());
//    }

    @DisplayName("토큰 파싱 테스트")
    @Test
    void parseToken() {
        String testEmail = "test@email.com";
        String testRole = "ROLE_TEST";
        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(10000L);
        String testAccessToken = jwtProvider.generateAccessToken(testMemberDto);

        Claims claims = jwtProvider.parseToken(testAccessToken);
        String role = (String) claims.get("role");
        String email = (String) claims.get("sub");

        assertNotNull(claims);
        assertNotNull(role);
        assertNotNull(email);
        assertEquals(testRole, role);
        assertEquals(testEmail, email);
    }

    @DisplayName("토큰 파싱 테스트 - 만료된")
    @Test
    void parseToken_expired() {
        String testEmail = "test@email.com";
        String testRole = "ROLE_TEST";
        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(1L);
        String testAccessToken = jwtProvider.generateAccessToken(testMemberDto);

        assertThrows(ExpiredJwtException.class, () -> jwtProvider.parseToken(testAccessToken));
    }

    @DisplayName("토큰 검증")
    @Test
    void isValidToken() {
        //given
        String testEmail = "test@email.com";
        String testRole = "ROLE_TEST";
        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(10000L);
        String testAccessToken = jwtProvider.generateAccessToken(testMemberDto);

        //when
        jwtProvider.validateToken(testAccessToken);

        //then
//        assertTrue(isValid);
    }

    @DisplayName("토큰 검증 - 만료된")
    @Test
    void isValidToken_expired() {
        //given
        String testEmail = "test@email.com";
        String testRole = "ROLE_TEST";
        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(1L);
        String testAccessToken = jwtProvider.generateAccessToken(testMemberDto);

        //when
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(testAccessToken));

        //then
        assertEquals("Expired JWT token", e.getMessage());
    }

    @DisplayName("토큰 검증 - 잘못된 형식")
    @Test
    void isValidToken_changed() {
        //given
        String testEmail = "test@email.com";
        String testRole = "ROLE_TEST";
        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(100000L);
        String testAccessToken = jwtProvider.generateAccessToken(testMemberDto);
        testAccessToken = testAccessToken.substring(1);

        //when
        String finalTestAccessToken = testAccessToken;
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(finalTestAccessToken));

        //then
        assertEquals("Malformed JWT token", e.getMessage());
    }


    @DisplayName("토큰 검증 - null")
    @Test
    void isValidToken_null() {
        //when
        InvalidTokenException e = assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(null));

        //then
        assertEquals("Invalid JWT token", e.getMessage());
    }

//    @DisplayName("토큰 유효기간 검증 - 만료되지 않은 토큰")
//    @Test
//    void isTokenExpired_freshToken() {
//        String testEmail = "test@email.com";
//        String testRole = "ROLE_TEST";
//        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
//        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(10000L);
//        String freshToken = jwtProvider.generateAccessToken(testMemberDto);
//
//        boolean isExpired = jwtProvider.isTokenExpired(freshToken);
//
//        assertFalse(isExpired);
//    }

//    @DisplayName("토큰 유효기간 검증 - 만료된 토큰")
//    @Test
//    void isTokenExpired_oldToken() {
////        String oldToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9URVNUIiwiaWF0IjoxNzM0MTU2OTc4LCJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsImV4cCI6MTczNDE1Njk3OH0.I2RWlygNPTzBFx882IeYKRfswMrA9S3cMU-Fib08WHI";
//        String testEmail = "test@email.com";
//        String testRole = "ROLE_TEST";
//        MemberDto testMemberDto = new MemberDto(testEmail, null, testRole);
//        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(1L);
//        String expiredToken = jwtProvider.generateAccessToken(testMemberDto);
//
//        boolean isExpired = jwtProvider.isTokenExpired(expiredToken);
//
//        assertTrue(isExpired);
//    }


    @Test
    void getAccessExpirationTime() {
        //given
        when(mockJwtProperties.getAccessExpirationTime()).thenReturn(900000L);

        //when
        long expiredTime = jwtProvider.getAccessExpirationTime();

        //then
        verify(mockJwtProperties).getAccessExpirationTime();
        assertEquals(900000L, expiredTime);
    }


    @Test
    void getRefreshExpirationTime() {
        //given
        when(mockJwtProperties.getRefreshExpirationTime()).thenReturn(604800000L);

        //when
        long expiredTime = jwtProvider.getRefreshExpirationTime();

        //then
        verify(mockJwtProperties).getRefreshExpirationTime();
        assertEquals(604800000L, expiredTime);

    }
}