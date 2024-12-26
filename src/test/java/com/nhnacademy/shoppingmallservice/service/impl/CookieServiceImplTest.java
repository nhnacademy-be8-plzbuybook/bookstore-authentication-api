package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.service.CookieService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class CookieServiceImplTest {
    private CookieService cookieService;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        cookieService = new CookieServiceImpl();
        response = new MockHttpServletResponse();
    }


    @Test
    void saveOnCookie() {
        // Given
        String key = "testKey";
        String value = "testValue";
        int expiry = 3600;

        // When
        cookieService.saveOnCookie(response, key, value, expiry);

        // Then
        Cookie[] cookies = response.getCookies();
        assertThat(cookies).isNotEmpty();

        Cookie cookie = cookies[0];
        assertThat(cookie.getName()).isEqualTo(key);
        assertThat(cookie.getValue()).isEqualTo(value);
        assertThat(cookie.getMaxAge()).isEqualTo(expiry);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
    }
}