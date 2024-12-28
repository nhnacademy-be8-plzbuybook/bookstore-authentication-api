package com.nhnacademy.shoppingmallservice.skm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyResponseDto {
    private Header header;
    private Body body;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header{
        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body{
        private String secret;
    }
}