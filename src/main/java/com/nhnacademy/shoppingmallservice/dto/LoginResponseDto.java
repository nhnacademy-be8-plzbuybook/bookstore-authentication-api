package com.nhnacademy.shoppingmallservice.dto;

public record LoginResponseDto(
        String accessToken,
        String memberStateName,
        String redirectUrl
){
}
