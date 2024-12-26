package com.nhnacademy.shoppingmallservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shoppingmallservice.common.handler.GlobalExceptionHandler;
import com.nhnacademy.shoppingmallservice.dto.AccessTokenReIssueRequestDto;
import com.nhnacademy.shoppingmallservice.enums.TokenType;
import com.nhnacademy.shoppingmallservice.service.CustomTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
class TokenControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private CustomTokenService tokenService;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TokenController(tokenService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void reIssueAccessToken() throws Exception {
        //given
        String email = "test@email.com";
        AccessTokenReIssueRequestDto request = new AccessTokenReIssueRequestDto(email);

        String reIssuedToken = "reIssued";
        when(tokenService.reissueAccessToken(email)).thenReturn(reIssuedToken);
        doNothing().when(tokenService).saveTokenOnCookie(response, TokenType.ACCESS, reIssuedToken);

        //when
        MvcResult result = mockMvc.perform(post("/api/auth/access-token/re-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertTrue(responseBody.contains(reIssuedToken));
        assertTrue(responseBody.contains("reIssuedAccessToken"));
    }

    @Test
    void reIssueAccessToken_invalid_request() throws Exception {
        //given
        String email = "wrongemail.com";
        AccessTokenReIssueRequestDto request = new AccessTokenReIssueRequestDto(email);

        //when
        mockMvc.perform(post("/api/auth/access-token/re-issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }
}