package com.zbutwialypiernik.flixage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AuthenticationApp {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApp.class, args);
    }

}
