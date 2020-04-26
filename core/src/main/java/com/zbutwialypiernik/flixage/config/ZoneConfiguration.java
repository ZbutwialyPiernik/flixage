package com.zbutwialypiernik.flixage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ZoneConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
