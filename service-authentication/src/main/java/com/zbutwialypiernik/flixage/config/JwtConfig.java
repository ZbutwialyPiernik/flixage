package com.zbutwialypiernik.flixage.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:application.properties")
public class JwtConfig {

    @Value("${jwt.access-token.expire-time:900}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token.expire-time:360000}")
    private Long refreshTokenExpireTime;

    @Value("${jwt.refresh-token.max-count:10}")
    private Short maxSessionCount;

    // TODO: refactor configuration and use HashiCorp Vault to manage configuration
    @Value("${jwt.private-key}")
    private String privateKey;

}
