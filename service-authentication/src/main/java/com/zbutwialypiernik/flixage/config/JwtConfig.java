package com.zbutwialypiernik.flixage.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@PropertySource("classpath:application.properties")
public class JwtConfig {

    @Value("${jwt.access-token.expire-time:15M}")
    private Duration accessTokenExpireTime;

    @Value("${jwt.refresh-token.expire-time:7D}")
    private Duration refreshTokenExpireTime;

    @Positive
    @Value("${jwt.refresh-token.max-count:10}")
    private Short maxSessionCount;

    // TODO: refactor configuration and use HashiCorp Vault to manage configuration
    @NotNull
    @Value("${jwt.private-key}")
    private String privateKey;

}
