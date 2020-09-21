package com.zbutwialypiernik.flixage.dto.authentication;

import lombok.Value;

@Value
public class AuthenticationResponse {

    String accessToken;

    String refreshToken;

    long expireTime;

}