package com.zbutwialypiernik.flixage.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Configuration
@PropertySource("classpath:application.properties")
public class JwtConfig {

    @Value("${jwt.access-token.expire-time:900}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token.expire-time:360000}")
    private Long refreshTokenExpireTime;

    @Value("${jwt.refresh-token.max-count:3}")
    private Short maxSessionCount;

    @Value("${jwt.private-key}")
    private String privateKey;

    @Value("${jwt.public-key}")
    private String publicKey;

}
