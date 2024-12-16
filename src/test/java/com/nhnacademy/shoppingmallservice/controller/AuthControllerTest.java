package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.common.handler.GlobalExceptionHandler;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(customTokenService, memberAuthService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_success() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "testPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);
        MemberDto mockMemberDto = mock(MemberDto.class);
        when(memberAuthService.authenticate(loginRequestDto)).thenReturn(mockMemberDto);
        doNothing().when(customTokenService).issueJwt(mock(HttpServletResponse.class), mockMemberDto);

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

//    @Test
//    void login_member_not_found() throws Exception {
//        LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@email.com", "testPassword");
//        String content = objectMapper.writeValueAsString(loginRequestDto);
//        when(memberAuthService.authenticate(loginRequestDto)).thenThrow(NotFoundException.class);
//
//        mockMvc.perform(post("/api/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(content))
//                .andExpect(status().isNotFound());
//    }

    @Test
    void login_member_wrong_password() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "wrongPassword");
        String content = objectMapper.writeValueAsString(loginRequestDto);
        when(memberAuthService.authenticate(loginRequestDto)).thenThrow(UnAuthorizedException.class);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}

