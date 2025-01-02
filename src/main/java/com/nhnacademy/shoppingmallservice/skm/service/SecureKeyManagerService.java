package com.nhnacademy.shoppingmallservice.skm.service;


import com.nhnacademy.shoppingmallservice.skm.dto.KeyResponseDto;
import com.nhnacademy.shoppingmallservice.skm.exception.KeyMangerException;
import com.nhnacademy.shoppingmallservice.skm.properties.SKMProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

@Service
public class SecureKeyManagerService {

    private final SKMProperties skmProperties;

    @Autowired
    public SecureKeyManagerService(SKMProperties skmProperties) {
        this.skmProperties = skmProperties; // app.skm의 설정 값 주입
    }

    public String fetchSecret(String keyId) {
        try {
            // keystore 설정 (PKCS12)
            KeyStore clientStore = KeyStore.getInstance("PKCS12");

            // keystrore 읽기
            InputStream keyStoreInputStream = new ClassPathResource(skmProperties.getKeystoreFile()).getInputStream();
            clientStore.load(keyStoreInputStream, skmProperties.getPassword().toCharArray());

            // TLS 설정
            SSLContext sslContext = SSLContextBuilder.create()
                    .setProtocol("TLS")
                    .loadKeyMaterial(clientStore, skmProperties.getPassword().toCharArray())
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            //keyId를 요청 경로에 적용
            URI uri = UriComponentsBuilder
                    .fromUriString(skmProperties.getUrl())
                    .path("/keymanager/v1.0/appkey/{appkey}/secrets/{keyid}")
                    .encode()
                    .build()
                    .expand(skmProperties.getAppKey(), keyId)
                    .toUri();

            // SKM API 호출
            ResponseEntity<KeyResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KeyResponseDto.class
            );

            if (response.getBody() != null && response.getBody().getBody() != null) {
                return response.getBody().getBody().getSecret(); // 복호화된 값 반환
            } else {
                throw new RuntimeException("Invalid response from Key Manager: " + response.getBody());
            }

        } catch (KeyStoreException | IOException | CertificateException |
                 NoSuchAlgorithmException | UnrecoverableKeyException |
                 KeyManagementException e) {
            throw new KeyMangerException(e.getMessage());
        }
    }
}