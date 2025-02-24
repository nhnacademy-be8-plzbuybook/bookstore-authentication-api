package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.common.handler.GlobalExceptionHandler;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.AccountService;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
class AuthControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomTokenService customTokenService;

    @MockBean
    private MemberAuthService memberAuthService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private MemberClient memberClient;

    private static final String LOGIN_API_URL = "/api/auth/login";
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(customTokenService, memberAuthService, accountService, memberClient))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_success() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);
        MemberDto mockMemberDto = mock(MemberDto.class);
        when(memberAuthService.authenticate(loginRequestDto)).thenReturn(mockMemberDto);
        when(customTokenService.issueAccessAndRefreshToken(mockMemberDto)).thenReturn("accessToken");

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    void login_invalid_parameter_email() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto(" ", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void login_invalid_parameter_password() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", " ");
        String content = objectMapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void login_member_not_found() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@email.com", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);
        when(memberAuthService.authenticate(loginRequestDto)).thenThrow(UnAuthorizedException.class);

        mockMvc.perform(post(LOGIN_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_member_wrong_password() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "wrongPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);
        when(memberAuthService.authenticate(loginRequestDto)).thenThrow(UnAuthorizedException.class);

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void getRoleFromToken_success() throws Exception {
        String token = "validToken";
        String role = "ADMIN";
        String authorizationHeader = "Bearer " + token;

        when(accountService.getRoleFromToken(token)).thenReturn(role);

        mockMvc.perform(get("/api/auth/role")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(content().string(role)); // `content()`를 사용하여 본문 내용 비교
    }

    @Test
    void getRoleFromToken_unauthorized() throws Exception {
        String token = "invalidToken";
        String authorizationHeader = "Bearer " + token;

        when(accountService.getRoleFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/auth/role")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEmailFromToken_success() throws Exception {
        String token = "validToken";
        String email = "test@email.com";
        String authorizationHeader = "Bearer " + token;

        when(accountService.getEmailFromToken(token)).thenReturn(email);

        mockMvc.perform(get("/api/auth/email")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(content().string(email)); // `content()`를 사용하여 본문 내용 비교
    }

    @Test
    void getEmailFromToken_unauthorized() throws Exception {
        String token = "invalidToken";
        String authorizationHeader = "Bearer " + token;

        when(accountService.getEmailFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/auth/email")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_member_dormant() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);

        // DORMANT 상태로 설정
        MemberDto mockMemberDto = mock(MemberDto.class);
        when(mockMemberDto.memberStateName()).thenReturn("DORMANT");
        when(memberAuthService.authenticate(loginRequestDto)).thenReturn(mockMemberDto);

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":null,\"memberStateName\":\"DORMANT\",\"redirectUrl\":\"/auth/verify-code\",\"role\":null}"));
    }

    // 마지막 로그인 업데이트 실패 시 로그 오류 처리 테스트
    @Test
    void login_last_login_update_fail() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);

        MemberDto mockMemberDto = mock(MemberDto.class);
        when(memberAuthService.authenticate(loginRequestDto)).thenReturn(mockMemberDto);
        when(customTokenService.issueAccessAndRefreshToken(mockMemberDto)).thenReturn("accessToken");

        // 마지막 로그인 업데이트 메서드에서 예외 발생하도록 설정
        doThrow(new RuntimeException("업데이트 실패")).when(memberClient).updateLastLogin(any());

        mockMvc.perform(post(LOGIN_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        // 로그 오류 확인 (ArgumentCaptor 사용)
        ArgumentCaptor<String> errorMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMemberDto, times(2)).memberStateName(); // 이 부분으로 로그가 실제로 찍혔는지 확인 가능
    }
}

