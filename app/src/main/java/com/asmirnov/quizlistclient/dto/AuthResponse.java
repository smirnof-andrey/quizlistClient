package com.asmirnov.quizlistclient.dto;

import com.asmirnov.quizlistclient.model.User;

public class AuthResponse {

    private User user;
    private String token;
    private String message;
    private int responseCode;    // 0 - user is found, 1 - user not found, 2 - token generate is fall, 3 - User exists (registration)

    public AuthResponse() {
        this.token = token;
    }

    public AuthResponse(String token, String message, int responseCode) {
        this.token = token;
        this.message = message;
        this.responseCode = responseCode;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
