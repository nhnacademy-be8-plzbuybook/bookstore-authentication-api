package com.nhnacademy.shoppingmallservice.webClient;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "BOOKSTORE")
public interface MemberClient {

    // 회원정보 없으면 예외처리
    @GetMapping("/api/members/email")
    MemberDto findMemberByEmail(@RequestParam("email") String email);

    //회원의 상태를 active로 수정하는 api
    @PostMapping("/api/members/{email}/active")
    ResponseEntity<String> activateMemberStatus(@PathVariable String email);
}
