package com.nhnacademy.shoppingmallservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AccessTokenReIssueRequestDto  (@NotBlank @JsonProperty("email") String email) {
}
