package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.JoinDTO;
import com.nhnacademy.shoppingmallservice.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {
    private final MemberService memberService;

    public JoinController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/members")
    public ResponseEntity<String> joinProcess(@RequestBody JoinDTO joinDTO) {
        memberService.joinProcess(joinDTO);
        return ResponseEntity.ok("회원가입 성공");
    }
}
