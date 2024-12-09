package com.nhnacademy.shoppingmallservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/shop/test")
    public String test() {
        return "test";
    }
}
