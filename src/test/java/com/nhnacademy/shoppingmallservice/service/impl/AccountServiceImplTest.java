package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MessagePayload;
import com.nhnacademy.shoppingmallservice.webClient.DooraySendClient;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> verifyRedisTemplate;

    @Mock
    private DooraySendClient dooraySendClient;

    @Mock
    private MemberClient memberClient;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(verifyRedisTemplate.opsForValue()).thenReturn(valueOperations);

    }

    @Test
    void testSendVerificationCode() {
        String userId = "testUser";
        String mockRandomCode = "1234"; // 인증 코드 Mock

        // accountService를 spy로 처리하여 메서드만 Mock 처리
        AccountServiceImpl spyAccountService = Mockito.spy(accountService);

        // randomCode 메서드 Mocking
        when(spyAccountService.randomCode()).thenReturn(mockRandomCode);

        // DooraySendClient의 sendMessage 메서드 호출을 검증하기 위한 설정
        doNothing().when(dooraySendClient).sendMessage(any(MessagePayload.class), anyLong(), anyLong(), anyString());

        // 메서드 실행
        spyAccountService.sendVerificationCode(userId);

        // ArgumentCaptor로 MessagePayload 캡처
        ArgumentCaptor<MessagePayload> captor = ArgumentCaptor.forClass(MessagePayload.class);
        verify(dooraySendClient, times(1)).sendMessage(captor.capture(), eq(3204376758577275363L), eq(3970575113227416519L), eq("9HZMjIarQxSv_l1CHIp39g"));

        // 캡처한 MessagePayload 객체를 확인
        MessagePayload capturedPayload = captor.getValue();
        assertEquals("회원 : " + userId, capturedPayload.getBotName());
        assertEquals("인증 코드 : " + mockRandomCode, capturedPayload.getText());

        // Redis set 호출을 Mocking 및 검증
        verify(verifyRedisTemplate.opsForValue(), times(1))
                .set(eq(userId), eq(mockRandomCode), eq(Duration.ofMinutes(5)));
    }

    @Test
    void testVerifyCode_Success() {
        String token = "testToken";
        String inputCode = "1234";
        String userId = "testUser";

        String redisKey = "fronteend:auth:" + token;

        when(valueOperations.get(redisKey)).thenReturn(userId);
        when(valueOperations.get(userId)).thenReturn(inputCode);

        boolean result = accountService.verifyCode(token, inputCode);


        assertTrue(result);
        verify(memberClient, times(1)).activateMemberStatus(userId);
    }

    @Test
    void testVerifyCode_InvalidToken() {
        String token = "invalidToken";
        String redisKey = "fronteend:auth:" + token;

        when(valueOperations.get(redisKey)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.verifyCode(token, "1234");
        });

        assertEquals("유효하지 않은 토큰!", exception.getMessage());
    }

    @Test
    void testGetRoleFromToken() {
        String token = "testToken";
        String role = "ROLE_USER";

        Claims claims = mock(Claims.class);
        when(jwtProvider.parseToken(token)).thenReturn(claims);
        when(claims.get("role", String.class)).thenReturn(role);

        String result = accountService.getRoleFromToken(token);

        assertEquals(role, result);
    }

    @Test
    void testGetRoleFromToken_InvalidToken() {
        String token = "invalidToken";
        when(jwtProvider.parseToken(token)).thenThrow(new IllegalArgumentException("Invalid token"));

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> {
            accountService.getRoleFromToken(token);
        });

        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void testGetEmailFromToken() {
        String token = "testToken";
        String email = "test@example.com";

        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);

        String result = accountService.getEmailFromToken(token);

        assertEquals(email, result);
    }
}