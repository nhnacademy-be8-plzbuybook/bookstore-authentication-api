package com.nhnacademy.shoppingmallservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
    @JsonProperty("id")
    private String userId;
    @JsonProperty("name")
    private String userName;

    public UserResponse(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
