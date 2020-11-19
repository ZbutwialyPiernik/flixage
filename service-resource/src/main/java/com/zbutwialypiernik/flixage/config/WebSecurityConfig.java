package com.zbutwialypiernik.flixage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationParser;
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
    private JwtAuthenticationParser parser;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("/eureka/**").hasRole(Role.ADMIN.name())
                        .antMatchers("/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), parser, mapper));
        // @formatter:on
    }

}
