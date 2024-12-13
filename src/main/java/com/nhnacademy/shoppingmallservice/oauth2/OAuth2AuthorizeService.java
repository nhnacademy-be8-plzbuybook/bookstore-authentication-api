package com.nhnacademy.shoppingmallservice.oauth2;

import com.nhnacademy.shoppingmallservice.dto.TokenDto;
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
public class OAuth2AuthorizeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PAYCO_AUTH_CODE_URL = "https://id.payco.com/oauth2.0/authorize";
    private final String RESPONSE_TYPE = "code"; // 페이코 인증 과정에 대한 구분값(code 로 고정)
    private final String CLIENT_ID = "3RD3UsjzB1Vz4wzS7dt4veD"; // TODO: 클라이언트의 ID
    private final String CLIENT_SECRET = "Ec_7BOIvDqa4eZzi7723exB8"; // TODO: 클라이언트의 비밀키
    private final String REDIRECT_URL = "http://test.com/login/oauth2/code/payco"; // TODO: 리다이렉트할 URL
    private final String TOKEN_URL = "https://id.payco.com/oauth2.0/token";
    private final String USER_INFO_URL = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json";
    private final String GRANT_TYPE = "authorization_code";
    private final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    public void redirectToOauth2LoginPage(HttpServletResponse response) {
        URI uri = UriComponentsBuilder
                .fromUriString(PAYCO_AUTH_CODE_URL)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("redirect_uri", REDIRECT_URL)
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

    // Access Token 발급
    public TokenDto getTokens(String code) {
        URI uri = UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("code", code)
                .queryParam("redirect_uri", REDIRECT_URL)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri.toString(), TokenDto.class);
    }

    // Refresh Token 발급
    public TokenDto getNewAccessToken(String refreshToken) {
        URI uri = UriComponentsBuilder
                .fromUriString(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE_REFRESH_TOKEN)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("refresh_token", refreshToken)
                .encode()
                .build()
                .toUri();

        return restTemplate.getForObject(uri.toString(), TokenDto.class);
    }

    // 토큰으로부터 Oauth 사용자 정보 가져오기
    public String getOAuthUserEmail(String accessToken) {
        URI uri = UriComponentsBuilder
                .fromUriString(USER_INFO_URL)
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, String> body = Map.of(
                "client_id", CLIENT_ID,
                "access_token", accessToken
        );

        HttpEntity<String> httpEntity = new HttpEntity(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Map.class);

            Map<String, Object> result = (Map<String, Object>) response.getBody();
            if (result == null || !result.containsKey("data")) {
                throw new RuntimeException("Payco 사용자 정보 응답이 비어 있습니다.");
            }

            Map<String, Object> data = (Map<String, Object>) result.get("data");
            Map<String, String> member = (Map<String, String>) data.get("member");

            return member.get("email");
        } catch (Exception e) {
            throw new RuntimeException("OAuth 정보를 가져오는 중 오류 발생");
        }
    }
}
