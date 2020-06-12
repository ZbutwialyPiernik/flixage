package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.config.JwtConfig;
import com.zbutwialypiernik.flixage.repository.TokenRepository;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class JwtServiceTest {

    private JwtConfig config = new JwtConfig();

    private TokenRepository tokenRepository;
    private DatabaseUserDetails userService;

    private Clock clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    public void user_does_not_get_authentication_when_credentials_does_not_match_any_user() {

    }


}
