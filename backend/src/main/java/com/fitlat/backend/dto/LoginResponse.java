package com.fitlat.backend.dto;

public class LoginResponse {

    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    public LoginResponse(
            UserResponse user,
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn
    ) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}