package com.zbutwialypiernik.flixage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private final String accessToken;

    private final String refreshToken;

    private final long expireTime;

}