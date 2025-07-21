package com.emart.invoice.dto;

import java.util.Map;

public class LoginResponse {
    private String token;
    private Map<String, Object> user;

    // Default constructor
    public LoginResponse() {}

    // Constructor with parameters
    public LoginResponse(String token, Map<String, Object> user) {
        this.token = token;
        this.user = user;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
