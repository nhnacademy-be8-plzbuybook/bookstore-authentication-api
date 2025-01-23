package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/api/auth/request-code")
    public String requestVerificationCode(@RequestParam("userId") String userId) {
        accountService.sendVerificationCode(userId);

        return "인증 코드가 발송 되었습니다.";
    }

    @PostMapping("/api/auth/verify-code")
    public String verifyVerificationCode(@RequestParam("token") String token, @RequestParam("code") String code) {
        boolean isValid = accountService.verifyCode(token, code);
        if(isValid) {
            return "인증이 완료 되었습니다";
        } else {
            return "인증 코드가 잘못되었습니다.";
        }

    }
}
