package com.zbutwialypiernik.flixage.config.security;

import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import io.jsonwebtoken.JwtParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtParser jwtParser;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .anyRequest().authenticated()
                .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtParser));
    }

}
