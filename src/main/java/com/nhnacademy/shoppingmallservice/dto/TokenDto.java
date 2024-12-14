package com.nhnacademy.shoppingmallservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDto(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") String expiresIn
) {
}
//액세스, 리프레쉬 두개만 살릴 수도
