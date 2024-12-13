package com.nhnacademy.shoppingmallservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenReIssueRequestDto  (@JsonProperty("email") String email,
                                             @JsonProperty("role") String role) {
}
