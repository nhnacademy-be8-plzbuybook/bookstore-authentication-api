package com.nhnacademy.shoppingmallservice.service;

import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    void saveOnCookie(HttpServletResponse response, String key, String value, int expiry);
}
