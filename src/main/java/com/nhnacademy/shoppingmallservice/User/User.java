package com.nhnacademy.shoppingmallservice.User;

import lombok.Getter;

import java.util.List;

@Getter
public class User {
    private final String userId;
    private final String userPassword;
    private final String userName;
    private final List<String> userRoles;

    public static enum ROLE{
        ROLE_ADMIN("관리자"),
        ROLE_USER("회원");

        ROLE(String role) {
        }
    }
    public static User createAdmin(String userId, String userName, String userPassword){
        return new User(userId,userName,userPassword,List.of(ROLE.ROLE_ADMIN.name()));
    }

    public static User createUser(String userId, String userName, String userPassword){
        return new User(userId,userName,userPassword,List.of(ROLE.ROLE_USER.name()));
    }

    private User(String userId, String userName, String userPassword, List<String> userRoles) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRoles = userRoles;
    }
}
