package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.util.KeyUtil;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthenticationConfiguration {

    // TODO: temp implementation, /public endpoint will be replaced with Spring Cloud Vault, it will prevent hardcoding secrets in authentication microservice.
    @Lazy
    @Bean("public-parser")
    public JwtParser publicParser(@Value("${JWT_PUBLIC_KEY}") String publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(KeyUtil.getRsaPublicKey(publicKey))
                .build();
    }

}
