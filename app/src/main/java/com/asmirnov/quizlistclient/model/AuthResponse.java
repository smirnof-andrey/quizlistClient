package com.asmirnov.quizlistclient.model;

public class AuthResponse {

    private Integer id;

    private User user;
    private String token;
    private String message;
    private int errorCode;    // 0 - user is found, 1 - user not found, 2 - token generate is fall, 3 - User exists (registration)

    public AuthResponse() {
        this.token = token;
    }

    public AuthResponse(String token, String message, int errorCode) {
        this.token = token;
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
