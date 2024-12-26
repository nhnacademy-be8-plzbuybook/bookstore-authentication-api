package com.nhnacademy.shoppingmallservice.webClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "frontClient")
public interface FrontClient {

    @GetMapping("/signup")
    void redirectToSignupPage(@RequestParam("email") String email);
}
