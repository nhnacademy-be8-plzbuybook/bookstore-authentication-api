package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceImplTest {
    @Mock
    private MemberClient memberClient;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberAuthServiceImpl memberAuthService;

    @Test
    void getMemberByEmail() {
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

    @Test
    void authenticate_success() {
        //given
        String email = "test@email.com";
        String rawPwd = "pwd";
        String encPwd = "pwd";
        MemberDto memberDto = new MemberDto(email, encPwd, "role");
        LoginRequestDto mockLoginRequestDto = mock(LoginRequestDto.class);
        when(mockLoginRequestDto.email()).thenReturn(email);
        when(memberClient.findMemberByEmail(email)).thenReturn(memberDto);
        when(mockLoginRequestDto.password()).thenReturn(rawPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(true);

        //when
        MemberDto result = memberAuthService.authenticate(mockLoginRequestDto);

        //then
        assertEquals(memberDto, result);
        verify(memberClient).findMemberByEmail(email);
        verify(mockLoginRequestDto).password();
        verify(passwordEncoder).matches(rawPwd, encPwd);
    }

    @Test
    void authenticate_member_not_found() {
        //given
        String email = "test@email.com";
        MemberDto mockMemberDto = mock(MemberDto.class);
        LoginRequestDto mockLoginRequestDto = mock(LoginRequestDto.class);

        when(mockLoginRequestDto.email()).thenReturn(email);
        when(memberClient.findMemberByEmail(email)).thenThrow(FeignException.NotFound.class);

        //when
        Exception e = assertThrows(UnAuthorizedException.class, () -> memberAuthService.authenticate(mockLoginRequestDto));

        //then
        assertEquals("wrong Id or Password", e.getMessage());
        verify(memberClient).findMemberByEmail(email);
        verify(mockLoginRequestDto, never()).password();
        verify(mockMemberDto, never()).password();
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_fail() {
        //given
        String email = "test@email.com";
        String rawPwd = "rawPwd";
        String encPwd = "encPwd";
        MemberDto memberDto = new MemberDto(email, encPwd, "role");
        LoginRequestDto mockLoginRequestDto = mock(LoginRequestDto.class);
        when(mockLoginRequestDto.email()).thenReturn(email);
        when(memberClient.findMemberByEmail(email)).thenReturn(memberDto);
        when(mockLoginRequestDto.password()).thenReturn(rawPwd);
        when(passwordEncoder.matches(rawPwd, encPwd)).thenReturn(false);

        //when
        Exception e = assertThrows(UnAuthorizedException.class, () -> memberAuthService.authenticate(mockLoginRequestDto));

        //then
        assertEquals("wrong Id or Password", e.getMessage());
        verify(memberClient).findMemberByEmail(email);
        verify(mockLoginRequestDto).password();
        verify(passwordEncoder).matches(rawPwd, encPwd);
    }

}