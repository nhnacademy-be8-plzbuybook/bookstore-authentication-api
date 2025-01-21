package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.properties.PaycoOauthProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaycoOauthServiceImplTest {

    @Mock
    private PaycoOauthProperties properties;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaycoOauthServiceImpl paycoOauthService;

    @Test
    void redirectToOauthLoginPage() throws Exception {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();
        String expectedUrl = "https://example.com/oauth?client_id=client_id&response_type&redirect_uri=http://redirect_uri&serviceProviderCode=FRIENDS&userLocale=ko_KR";

        when(properties.getCodeUrl()).thenReturn("https://example.com/oauth");
        when(properties.getClientId()).thenReturn("client_id");
        when(properties.getRedirectUrl()).thenReturn("http://redirect_uri");

        // when
        paycoOauthService.redirectToOauthLoginPage(response);

        // then
        assertEquals(expectedUrl, response.getRedirectedUrl());
    }

//    @Test
//    void getTokens() {
//        // given
//        String code = "testCode";
//        TokenDto tokenDto = new TokenDto("Bearer", "accessToken", "refreshToken", "3600");
//        String expectedUri = "https://example.com/token?grant_type=authorization_code&client_id=client_id&client_secret=client_secret&code=testCode&redirect_uri=http://redirect_uri";
//
//        // PaycoOauthProperties mock 설정
//        when(properties.getTokenUrl()).thenReturn("https://example.com/token");
//        when(properties.getClientId()).thenReturn("client_id");
//        when(properties.getClientSecret()).thenReturn("client_secret");
//        when(properties.getRedirectUrl()).thenReturn("http://redirect_uri");
//
//        // RestTemplate을 mock하여 응답값을 설정
//        when(restTemplate.getForObject(eq(expectedUri), eq(TokenDto.class))).thenReturn(tokenDto);
//
//        // when
//        TokenDto result = paycoOauthService.getTokens(code);
//
//        // then
//        assertNotNull(result);
//        assertEquals("accessToken", result.accessToken());
//        assertEquals("refreshToken", result.refreshToken());
//        assertEquals("Bearer", result.tokenType());
//        assertEquals("3600", result.expiresIn());
//    }

//    @Test
//    void getOAuthUserEmail() {
//        // given
//        String accessToken = "accessToken";
//        String expectedEmail = "user@example.com";
//        String expectedUri = "https://example.com/userinfo"; // user info URL 설정
//
//        // Mock PaycoOauthProperties
//        when(properties.getUserInfoUrl()).thenReturn(expectedUri); // userInfoUrl 값 설정
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//        headers.set("client_id", "client_id");
//        headers.set("access_token", accessToken);
//
//        // Mock ResponseEntity
//        ResponseEntity<Map> responseEntity = mock(ResponseEntity.class);
//        Map<String, Object> responseBody = mock(Map.class);
//        Map<String, String> member = mock(Map.class);
//        when(member.get("email")).thenReturn(expectedEmail);
//        when(responseBody.get("data")).thenReturn(mock(Map.class));
//        when(responseBody.get("data")).thenReturn(member); // 중복된 when 구문 제거
//        when(responseEntity.getBody()).thenReturn(responseBody);
//        when(restTemplate.exchange(expectedUri, HttpMethod.POST, new HttpEntity<>(null, headers), Map.class)).thenReturn(responseEntity);
//
//        // when
//        String email = paycoOauthService.getOAuthUserEmail(accessToken);
//
//        // then
//        assertNotNull(email);
//        assertEquals(expectedEmail, email);
//    }
}