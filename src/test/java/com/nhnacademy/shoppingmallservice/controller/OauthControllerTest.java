package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.dto.OauthLoginResponseDto;
import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import com.nhnacademy.shoppingmallservice.service.impl.CustomTokenServiceImpl;
import com.nhnacademy.shoppingmallservice.service.impl.PaycoOauthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OauthController.class)
class OauthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PaycoOauthServiceImpl paycoOauthService; // MockBean을 사용하여 Mock 객체를 생성합니다.

    @MockBean
    private MemberAuthService memberAuthService;

    @MockBean
    private CustomTokenServiceImpl tokenService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // WebMvcTest의 테스트 환경에 맞게 MockMvc 객체를 설정합니다.
        mockMvc = MockMvcBuilders.standaloneSetup(new OauthController(memberAuthService, paycoOauthService, tokenService))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void oauthLogin_success() throws Exception {
        String code = "validCode";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String email = "test@email.com";

        TokenDto tokenDto = new TokenDto("Bearer", accessToken, refreshToken, "3600");
        MemberDto memberDto = new MemberDto("testUser", email, "ACTIVE", "USER");

        // Mock service methods
        when(paycoOauthService.getTokens(code)).thenReturn(tokenDto);
        when(paycoOauthService.getOAuthUserEmail(tokenDto.accessToken())).thenReturn(email);
        when(memberAuthService.getMemberByEmail(email)).thenReturn(Optional.of(memberDto));
        when(tokenService.issueAccessAndRefreshToken(memberDto)).thenReturn(accessToken);

        mockMvc.perform(get("/api/auth/oauth/login")
                        .param("code", code))
                .andExpect(status().isOk());
    }

    @Test
    void oauthLogin_memberNotFound() throws Exception {
        String code = "validCode";
        String accessToken = "accessToken";
        String email = "test@email.com";

        TokenDto tokenDto = new TokenDto("Bearer", accessToken, "refreshToken", "3600");

        // Mock service methods
        when(paycoOauthService.getTokens(code)).thenReturn(tokenDto);
        when(paycoOauthService.getOAuthUserEmail(tokenDto.accessToken())).thenReturn(email);
        when(memberAuthService.getMemberByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/oauth/login")
                        .param("code", code))
                .andExpect(status().isOk());
    }
}