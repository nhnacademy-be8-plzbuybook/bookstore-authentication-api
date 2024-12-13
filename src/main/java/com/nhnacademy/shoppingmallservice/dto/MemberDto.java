package com.nhnacademy.shoppingmallservice.dto;

public record MemberDto(
        String email,
        String password,
        String role
) {
}
