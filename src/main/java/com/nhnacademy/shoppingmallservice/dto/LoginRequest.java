package com.nhnacademy.shoppingmallservice.dto;

public class LoginRequest {
    private String id;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
