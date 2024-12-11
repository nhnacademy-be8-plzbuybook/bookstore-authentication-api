package com.nhnacademy.shoppingmallservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @GetMapping("/hello")
    public String hello() {
        return "여기는 인증서버 관제탑 응답하라";
    }
}
