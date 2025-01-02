package com.nhnacademy.shoppingmallservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessagePayload {
    private String botName;
    private String text;
}
