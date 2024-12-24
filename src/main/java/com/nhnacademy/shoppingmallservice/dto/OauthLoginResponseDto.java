package com.nhnacademy.shoppingmallservice.dto;

public record OauthLoginResponseDto (boolean isRegistered, String email, String accessToken){
}
