package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;

public interface MemberAuthService {
    MemberDto getMemberByEmail(String email);
    MemberDto authenticate(LoginRequestDto loginRequest);
}
