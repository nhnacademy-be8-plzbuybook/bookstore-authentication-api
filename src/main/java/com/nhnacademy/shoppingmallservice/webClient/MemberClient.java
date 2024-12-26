package com.nhnacademy.shoppingmallservice.webClient;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "BOOKSTORE-DEV") // 유레카에 등록된 서점 api 서비스 이름
public interface MemberClient {

    // 회원정보 없으면 예외처리
    @GetMapping("/api/members/email")
    MemberDto findMemberByEmail(@RequestParam("email") String email);
}
