package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import com.nhnacademy.shoppingmallservice.service.MemberAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .build();
    }

    @Test
    void login_success() throws Exception {
//        String path = "/api/login";
//        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "testPassword");
//        String content = objectMapper.writeValueAsString(loginRequestDto);
//        when(memberAuthService.getMemberByEmail("test@email.com")).thenReturn()
//        mockMvc.perform(post(path)
//                .contentType("application/json")
//                .content(content))
//                .andExpect(status().isOk());
    }
}

