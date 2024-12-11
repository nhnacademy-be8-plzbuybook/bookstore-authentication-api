package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.dto.JoinDTO;
import com.nhnacademy.shoppingmallservice.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class JoinController {
    private final MemberService memberService;

    public JoinController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/members")
    public String joinProcess(JoinDTO joinDTO) {
        memberService.joinProcess(joinDTO);
        return "ok";
    }
}
