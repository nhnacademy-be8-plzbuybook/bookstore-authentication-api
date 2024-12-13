package com.nhnacademy.shoppingmallservice.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(@NotNull String email,
                              @NotNull String password) {


}
