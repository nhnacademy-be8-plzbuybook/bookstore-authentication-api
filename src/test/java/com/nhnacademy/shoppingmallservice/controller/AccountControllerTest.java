package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable();
            return http.build();
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testRequestVerificationCode() throws Exception {
        String userId = "testUser";

        doNothing().when(accountService).sendVerificationCode(userId);

        mockMvc.perform(post("/api/auth/request-code")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())  // 200 OK 상태 확인
                .andExpect(content().string("인증 코드가 발송 되었습니다."));

        verify(accountService, times(1)).sendVerificationCode(userId);
    }


    @Test
    public void testVerifyVerificationCode_Success() throws Exception {
        String token = "validToken";
        String code = "123456";

        when(accountService.verifyCode(token, code)).thenReturn(true);

        mockMvc.perform(post("/api/auth/verify-code")
                        .param("token", token)
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("인증이 완료 되었습니다"));
        verify(accountService, times(1)).verifyCode(token, code);
    }

    @Test
    public void testVerifyVerificationCode_Failure() throws Exception {
        String token = "validToken";
        String code = "wrongCode";

        when(accountService.verifyCode(token, code)).thenReturn(false);

        mockMvc.perform(post("/api/auth/verify-code")
                        .param("token", token)
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 잘못되었습니다."));

        verify(accountService, times(1)).verifyCode(token, code);
    }
}