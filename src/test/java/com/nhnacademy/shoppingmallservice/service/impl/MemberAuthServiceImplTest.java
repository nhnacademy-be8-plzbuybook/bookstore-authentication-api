package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.LoginFailException;
import com.nhnacademy.shoppingmallservice.common.exception.UnAuthorizedException;
import com.nhnacademy.shoppingmallservice.dto.LoginRequestDto;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceImplTest {
    @Mock
    private MemberClient memberClient;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberAuthServiceImpl memberAuthService;

    @Test
    void getMemberByEmail_found() {
        //given
        String email = "test@email.com";
        MemberDto mockMemberDto = mock(MemberDto.class);

        when(memberClient.findMemberByEmail(email)).thenReturn(mockMemberDto);

        //when
        Optional<MemberDto> optionalMemberDto = memberAuthService.getMemberByEmail(email);

        assertTrue(optionalMemberDto.isPresent());
        verify(memberClient).findMemberByEmail(email);
    }

    @Test
    void getMemberByEmail_not_found() {
        //given
        String email = "test@email.com";

        when(memberClient.findMemberByEmail(email)).thenThrow(FeignException.NotFound.class);

        //when
        Optional<MemberDto> optionalMemberDto = memberAuthService.getMemberByEmail(email);

        assertTrue(optionalMemberDto.isEmpty());
        verify(memberClient).findMemberByEmail(email);
    }

//    @Disabled
    @Test
    void authenticate_success() {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "test");
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "ACTIVE");

        when(memberClient.findMemberByEmail("test@email.com")).thenReturn(memberDto);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        //when
        MemberDto result = memberAuthService.authenticate(loginRequestDto);

        //then
        assertEquals(memberDto, result);
        verify(memberClient).findMemberByEmail("test@email.com");
    }

    @Test
    void authenticate_member_not_found() {
        //given
        String email = "test@email.com";
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "test");

        when(memberClient.findMemberByEmail(email)).thenThrow(FeignException.NotFound.class);

        //when
        Exception e = assertThrows(UnAuthorizedException.class, () -> memberAuthService.authenticate(loginRequestDto));

        //then
        assertEquals("회원 정보가 존재하지 않습니다.", e.getMessage());
        verify(memberClient).findMemberByEmail(email);
    }
    @Test
    void authenticate_member_withdrawal() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "test");
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "WITHDRAWAL");

        when(memberClient.findMemberByEmail("test@email.com")).thenReturn(memberDto);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when
        Exception e = assertThrows(LoginFailException.class, () -> memberAuthService.authenticate(loginRequestDto));

        // then
        assertEquals("이미 탈퇴한 회원입니다.", e.getMessage());
        verify(memberClient).findMemberByEmail("test@email.com");
    }

//    @Disabled
    @Test
    void authenticate_fail() {
        //given
        LoginRequestDto loginRequestDto = spy(new LoginRequestDto("test@email.com", "wrongPwd"));
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER", "ACTIVE");

        when(memberClient.findMemberByEmail("test@email.com")).thenReturn(memberDto);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //when
        Exception e = assertThrows(UnAuthorizedException.class, () -> memberAuthService.authenticate(loginRequestDto));

        //then
        assertEquals("잘못된 아이디 또는 비밀번호입니다.", e.getMessage());
        verify(memberClient).findMemberByEmail(loginRequestDto.email());
        verify(loginRequestDto).password();
    }

}