package com.nhnacademy.shoppingmallservice.skm.service;

import com.nhnacademy.shoppingmallservice.skm.dto.KeyResponseDto;
import com.nhnacademy.shoppingmallservice.skm.exception.KeyMangerException;
import com.nhnacademy.shoppingmallservice.skm.properties.SKMProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SecureKeyManagerServiceTest {
    private SecureKeyManagerService secureKeyManagerService;
    private SKMProperties skmProperties;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        // SKMProperties Mock 설정
        skmProperties = Mockito.mock(SKMProperties.class);
        when(skmProperties.getKeystoreFile()).thenReturn("skm.p12");
        when(skmProperties.getPassword()).thenReturn("4444");
        when(skmProperties.getUrl()).thenReturn("https://api-keymanager.nhncloudservice.com");
        when(skmProperties.getAppKey()).thenReturn("qTQNj7LyHhdAazH3");

        // Mock RestTemplate 대체
        restTemplate = Mockito.mock(RestTemplate.class);

        // 클래스 인스턴스 생성
        secureKeyManagerService = new SecureKeyManagerService(skmProperties);
    }

    @Test
    @DisplayName("url secretFetch 성공")
    void fetchSecret_url_Success() {
        // 준비: KeyResponseDto Mock 설정
        KeyResponseDto responseDto = new KeyResponseDto();
        KeyResponseDto.Body body = new KeyResponseDto.Body();
        body.setSecret("jdbc:mysql://220.67.216.14:13306/project_be8_plzbuybook_bookstore");
        responseDto.setBody(body);

        ResponseEntity<KeyResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        // RestTemplate Mock 동작 설정
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(KeyResponseDto.class))
        ).thenReturn(responseEntity);

        // 실행
        String secret = secureKeyManagerService.fetchSecret("1117490ed9294c8798e83f2cb162982d");

        // 검증
        assertNotNull(secret);
        assertEquals("jdbc:mysql://220.67.216.14:13306/project_be8_plzbuybook_bookstore", secret);
    }

    @Test
    @DisplayName("username secretFetch 성공")
    void fetchSecret_username_Success() {
        // 준비: KeyResponseDto Mock 설정
        KeyResponseDto responseDto = new KeyResponseDto();
        KeyResponseDto.Body body = new KeyResponseDto.Body();
        body.setSecret("project_be8_plzbuybook");
        responseDto.setBody(body);

        ResponseEntity<KeyResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        // RestTemplate Mock 동작 설정
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(KeyResponseDto.class))
        ).thenReturn(responseEntity);

        // 실행
        String secret = secureKeyManagerService.fetchSecret("7f2f713f0a6c4d82acb64ed3aa831cee");

        // 검증
        assertNotNull(secret);
        assertEquals("project_be8_plzbuybook", secret);
    }

    @Test
    @DisplayName("password secretFetch 성공")
    void fetchSecret_password_Success() {
        // 준비: KeyResponseDto Mock 설정
        KeyResponseDto responseDto = new KeyResponseDto();
        KeyResponseDto.Body body = new KeyResponseDto.Body();
        body.setSecret("MOW6c#y4TVxi1P5b");
        responseDto.setBody(body);

        ResponseEntity<KeyResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        // RestTemplate Mock 동작 설정
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(KeyResponseDto.class))
        ).thenReturn(responseEntity);

        // 실행
        String secret = secureKeyManagerService.fetchSecret("7ac2c65487d34a7fa73997fada11d730");

        // 검증
        assertNotNull(secret);
        assertEquals("MOW6c#y4TVxi1P5b", secret);
    }

    @Test
    void fetchSecret_shouldThrowException_whenKeyManagerReturnsInvalidResponse() throws Exception {
        // 준비: KeyResponseDto 내용이 null인 경우
        ResponseEntity<KeyResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        // RestTemplate Mock 동작 설정
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(KeyResponseDto.class))
        ).thenReturn(responseEntity);

        // 실행 & 검증
        Exception exception = assertThrows(RuntimeException.class, () -> {
            secureKeyManagerService.fetchSecret("testKeyId");
        });

        assertTrue(exception.getMessage().contains("Invalid response from Key Manager"));
    }

    @Test
    void fetchSecret_shouldThrowKeyManagerException_whenKeyStoreInitializationFails() throws Exception {
        // 준비: keystore 읽기 중 IOException 강제 발생
        when(skmProperties.getKeystoreFile()).thenThrow(new KeyMangerException("Keystore file not found."));

        // 실행 & 검증
        Exception exception = assertThrows(KeyMangerException.class, () -> {
            secureKeyManagerService.fetchSecret("testKeyId");
        });

        assertTrue(exception.getMessage().contains("Keystore file not found."));
    }
}

