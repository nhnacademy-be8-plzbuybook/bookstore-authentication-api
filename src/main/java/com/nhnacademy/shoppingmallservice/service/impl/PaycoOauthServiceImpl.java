package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.dto.TokenDto;
import com.nhnacademy.shoppingmallservice.properties.PaycoOauthProperties;
import com.nhnacademy.shoppingmallservice.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaycoOauthServiceImpl implements OauthService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final PaycoOauthProperties properties;

    public void redirectToOauthLoginPage(HttpServletResponse response) {
        URI uri = UriComponentsBuilder
                .fromUriString(properties.getCodeUrl())
                .queryParam("client_id", properties.getClientId())
                .queryParam("response_type", properties.getResponseType())
                .queryParam("redirect_uri", properties.getRedirectUrl())
                .queryParam("serviceProviderCode", "FRIENDS")
                .queryParam("userLocale", "ko_KR")
                .encode()
                .build()
                .toUri();
        try {
            response.sendRedirect(uri.toString());
        } catch (IOException e) {
            throw new RuntimeException("Oauth2 로그인 페이지 리다이렉트 실패", e);
        }
    }

    // Refresh Token 발급
//    public TokenDto getNewAccessToken(String refreshToken) {
//        URI uri = UriComponentsBuilder
//                .fromUriString(TOKEN_URL)
//                .queryParam("grant_type", GRANT_TYPE_REFRESH_TOKEN)
//                .queryParam("client_id", CLIENT_ID)
//                .queryParam("client_secret", CLIENT_SECRET)
//                .queryParam("refresh_token", refreshToken)
//                .encode()
//                .build()
//                .toUri();
//
//        return restTemplate.getForObject(uri.toString(), TokenDto.class);
//    }

    // Access Token 발급
    public TokenDto getTokens(String code) {
        URI uri = UriComponentsBuilder
                .fromUriString(properties.getTokenUrl())
                .queryParam("grant_type", properties.getGrantType())
                .queryParam("client_id", properties.getClientId())
                .queryParam("client_secret", properties.getClientSecret())
                .queryParam("code", code)
                .queryParam("redirect_uri", properties.getRedirectUrl())
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri.toString(), TokenDto.class);
    }

    public String getOAuthUserEmail(String accessToken) {
        URI uri = UriComponentsBuilder
                .fromUriString(properties.getUserInfoUrl())
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("client_id", properties.getClientId());
        headers.set("access_token", accessToken);

        try {
            ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity(null, headers), Map.class);
            return extractEmail(response);
        } catch (Exception e) {
            throw new RuntimeException("OAuth 정보를 가져오는 중 오류 발생");
        }
    }

    public String extractEmail(ResponseEntity<?> response) {
        Map<String, Object> result = (Map<String, Object>) response.getBody();
        if (result == null || !result.containsKey("data")) {
            throw new RuntimeException("Payco 사용자 정보 응답이 비어 있습니다.");
        }
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        Map<String, String> member = (Map<String, String>) data.get("member");

        return member.get("email");
    }
}
