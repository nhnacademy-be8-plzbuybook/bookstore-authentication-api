//package com.nhnacademy.shoppingmallservice.controller;
//
//import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
//import com.nhnacademy.shoppingmallservice.dto.MessagePayload;
//import com.nhnacademy.shoppingmallservice.service.impl.AccountServiceImpl;
//import com.nhnacademy.shoppingmallservice.webClient.DooraySendClient;
//import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.time.Duration;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class AccountControllerTest {
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private DooraySendClient dooraySendClient;
//
//    @Mock
//    private MemberClient memberClient;
//
//    @Mock
//    private JwtProvider jwtProvider;
//
//    @InjectMocks
//    private AccountServiceImpl accountService;
//
//    @Test
//    void sendVerificationCode_ShouldSendCodeAndStoreInRedis() {
//        String userId = "testUser";
//        ArgumentCaptor<String> redisKeyCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<MessagePayload> messageCaptor = ArgumentCaptor.forClass(MessagePayload.class);
//
//        // 실행
//        accountService.sendVerificationCode(userId);
//
//        // 검증
//        verify(redisTemplate).opsForValue().set(redisKeyCaptor.capture(), codeCaptor.capture(), eq(Duration.ofMinutes(5)));
//        verify(dooraySendClient).sendMessage(messageCaptor.capture(), anyLong(), anyLong(), anyString());
//
//        String redisKey = redisKeyCaptor.getValue();
//        String code = codeCaptor.getValue();
//        MessagePayload payload = messageCaptor.getValue();
//
//        assertThat(redisKey).isEqualTo(userId);
//        assertThat(code).matches("\\d{4}");
//        assertThat(payload.getBotName()).isEqualTo("회원 : " + userId);
//        assertThat(payload.getText()).contains("인증 코드 : ");
//    }
//}