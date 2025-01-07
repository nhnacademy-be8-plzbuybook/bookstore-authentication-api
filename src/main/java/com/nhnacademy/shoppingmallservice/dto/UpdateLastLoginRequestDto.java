package com.nhnacademy.shoppingmallservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLastLoginRequestDto {
    @NotNull
    private String email;
    @NotNull
    private LocalDateTime lastLogin;
}