package com.nhnacademy.shoppingmallservice.service;

import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;

import java.util.Optional;

public interface MemberAuthService {
    Optional<MemberDto> getMemberByEmail(String email);
    MemberDto authenticate(LoginRequestDto loginRequest);
}
