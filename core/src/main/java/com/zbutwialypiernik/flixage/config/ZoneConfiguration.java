package com.zbutwialypiernik.flixage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.util.TimeZone;

@Configuration
public class ZoneConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
