package com.zbutwialypiernik.flixage.config;

import com.netflix.appinfo.InstanceInfo;
import com.zbutwialypiernik.flixage.util.KeyUtil;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.passay.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.http.RestTemplateEurekaHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
* Shared configuration security between projects, to avoid code duplication and mistakes.
*/
@Log4j2
@Configuration
public class GlobalSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(Arrays.asList(
                new LengthRule(8, 128),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new WhitespaceRule()
                ));
    }

    // TODO: temp implementation, /public endpoint will be replaced with Spring Cloud Vault, it will prevent hardcoding secrets in authentication microservice.
    @Lazy
    @Bean("public-parser")
    public JwtParser publicParser(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        // Get random authentication microservice instance and gain public key from API.
        List<ServiceInstance> instances = discoveryClient.getInstances("authentication-server");

        if (instances.isEmpty()) {
            throw new IllegalStateException("Authentication server not found!");
        }

        ServiceInstance instanceInfo = instances.get(new Random().nextInt(instances.size()));

        String endpoint = "http://" + instanceInfo.getHost() + ":" + instanceInfo.getPort() + "/authentication/public";
        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Public key not found");
        }

        return Jwts.parserBuilder()
                .setSigningKey(KeyUtil.getRsaPublicKey(response.getBody()))
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
