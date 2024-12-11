package com.nhnacademy.shoppingmallservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestLoginController {

    @GetMapping("/api/login")
    public String login() {
        return "login";
    }
}
