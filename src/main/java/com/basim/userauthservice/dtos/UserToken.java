package com.basim.userauthservice.dtos;

import com.basim.userauthservice.models.User;

public class UserToken {

    private User user;

    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

}
