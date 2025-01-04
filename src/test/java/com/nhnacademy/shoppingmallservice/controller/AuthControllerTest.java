package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.common.handler.GlobalExceptionHandler;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.AccountService;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private static final String LOGIN_API_URL = "/api/auth/login";
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(customTokenService, memberAuthService, accountService))
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
}

