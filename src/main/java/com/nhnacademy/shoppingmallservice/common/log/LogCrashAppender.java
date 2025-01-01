package com.nhnacademy.shoppingmallservice.common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Setter
@Slf4j
public class LogCrashAppender extends AppenderBase<ILoggingEvent> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();

    private String url;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {

        LogCrashRequest request = new LogCrashRequest(iLoggingEvent.getFormattedMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            String str = objectMapper.writeValueAsString(request);
            HttpEntity<String> body = new HttpEntity<>(str, headers);
            restTemplate.postForEntity(url, body, String.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
