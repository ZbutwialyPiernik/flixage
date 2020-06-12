package com.zbutwialypiernik.flixage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.time.Clock;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationParser parser;

    @Autowired
    private Clock clock;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("api/tracks/*/stream").permitAll() // TODO: remove that after i will resolve problem with authorization headers in Flutter audio player
                        .anyRequest().authenticated()
                .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), parser, mapper, clock));
    }

}
