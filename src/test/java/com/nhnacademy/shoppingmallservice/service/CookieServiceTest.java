package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.service.impl.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CookieServiceTest {
    private final CookieService cookieService = new CookieServiceImpl();

    @Test
    void saveTokenCookie() { // 테스트 더 필요할 것 같음
        //given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        String key = "key";
        String value = "value";
        int expiry = 1000;
        doNothing().when(mockResponse).addCookie(any(Cookie.class));

        //when
        cookieService.saveOnCookie(mockResponse, key, value, expiry);

        //then
        verify(mockResponse).addCookie(any(Cookie.class));
    }
}