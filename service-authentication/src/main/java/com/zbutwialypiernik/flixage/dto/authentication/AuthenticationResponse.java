package com.zbutwialypiernik.flixage.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
public class AuthenticationResponse {

    private String accessToken;

    private String refreshToken;

    private long expireTime;

    public AuthenticationResponse(String accessToken, String refreshToken, Duration expireTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime.toSeconds();
    }

    public Duration getExpireTime() {
        return Duration.ofSeconds(expireTime);
    }

    public void setExpireTime(Duration expireTime) {
        this.expireTime = expireTime.toSeconds();
    }
}