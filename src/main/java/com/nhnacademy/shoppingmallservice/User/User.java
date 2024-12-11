package com.nhnacademy.shoppingmallservice.User;

import java.util.List;

public class User {
    private final String userId;
    private final String userPassword;
    private final String userName;
    private final List<String> userRoles;

    public static enum ROLE {
        ROLE_ADMIN("ADMIN"),
        ROLE_USER("USER");

        private final String role;

        ROLE(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

    public static User createAdmin(String userId, String userName, String userPassword) {
        return new User(userId, userName, userPassword, List.of(ROLE.ROLE_ADMIN.name()));
    }

    public static User createUser(String userId, String userName, String userPassword) {
        return new User(userId, userName, userPassword, List.of(ROLE.ROLE_USER.name()));
    }

    private User(String userId, String userName, String userPassword, List<String> userRoles) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRoles = userRoles;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }


}
