package com.nhnacademy.shoppingmallservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record MemberDto(
        @NotNull String email,
        @Nullable String password,
        @NotNull String role
) {
}
