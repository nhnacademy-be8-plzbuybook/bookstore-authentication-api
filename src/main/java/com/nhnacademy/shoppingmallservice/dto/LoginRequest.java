package com.nhnacademy.shoppingmallservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@Getter
@ToString
public class LoginRequest {
    private  String id;
    private  String password;
}
