package com.zbutwialypiernik.flixage.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.time.Duration;

@Value
public class AuthenticationResponse {

    String accessToken;

    String refreshToken;

    long expireTime;

}