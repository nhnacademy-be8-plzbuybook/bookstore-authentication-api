package com.nhnacademy.shoppingmallservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberDto(
        @NotNull String email,
        @Nullable String password,
        @NotNull @JsonProperty("authName") String role,
        @NotNull String memberStateName
) {
}
