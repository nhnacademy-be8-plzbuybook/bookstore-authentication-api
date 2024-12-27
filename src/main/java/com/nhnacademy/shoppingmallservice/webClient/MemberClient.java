package com.nhnacademy.shoppingmallservice.webClient;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "GATEWAY")
public interface MemberClient {

    // 회원정보 없으면 예외처리
    @GetMapping("/api/members/email")
    MemberDto findMemberByEmail(@RequestParam("email") String email);
}
