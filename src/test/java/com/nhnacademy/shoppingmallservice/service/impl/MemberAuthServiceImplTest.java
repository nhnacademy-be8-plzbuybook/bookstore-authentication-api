package com.nhnacademy.shoppingmallservice.service.impl;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceImplTest {
    @Mock
    private MemberClient memberClient;
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
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "test");
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");

        when(memberClient.findMemberByEmail("test@email.com")).thenReturn(memberDto);

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
        assertEquals("wrong Id or Password", e.getMessage());
        verify(memberClient).findMemberByEmail(email);
    }

    @Test
    void authenticate_fail() {
        //given
        LoginRequestDto loginRequestDto = spy(new LoginRequestDto("test@email.com", "wrongPwd"));
        MemberDto memberDto = new MemberDto("test@email.com", "test", "ROLE_MEMBER");

        when(memberClient.findMemberByEmail("test@email.com")).thenReturn(memberDto);

        //when
        Exception e = assertThrows(UnAuthorizedException.class, () -> memberAuthService.authenticate(loginRequestDto));

        //then
        assertEquals("wrong Id or Password", e.getMessage());
        verify(memberClient).findMemberByEmail(loginRequestDto.email());
        verify(loginRequestDto).password();
    }

}