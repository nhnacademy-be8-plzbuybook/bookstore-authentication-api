package com.nhnacademy.shoppingmallservice.common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LogCrashAppenderTest {

    @Mock
    private RestTemplate restTemplate; // RestTemplate Mock

    @InjectMocks
    private LogCrashAppender logCrashAppender;

    private final String url = "http://localhost:8080/crash-log"; // 예시 URL

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
        logCrashAppender.setUrl(url); // 테스트할 URL 설정
    }

    @Test
    void testAppendWithJsonProcessingException() throws JsonProcessingException {
        // Given
        ILoggingEvent mockLoggingEvent = mock(ILoggingEvent.class);
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("Test crash log");

        // ObjectMapper를 Mock으로 설정하여 예외를 발생시킴
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        // When & Then
        // 예외가 발생해야 하므로, 테스트가 성공하면 예외를 잡아서 확인
        assertThrows(RuntimeException.class, () -> logCrashAppender.append(mockLoggingEvent));
    }
}