package com.nhnacademy.shoppingmallservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDto(
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") String expiresIn
) {
}
