package com.zbutwialypiernik.flixage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DiscoverySecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.user.name:discovery}")
    private String defaultUsername;

    @Value("${spring.security.user.password:Passw0rd}")
    private String defaultPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            .and().authorizeRequests()
                .anyRequest().hasRole("SYSTEM")
            .and()
                .httpBasic()
            .and()
                .csrf().disable();
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser(defaultUsername)
                .password(passwordEncoder.encode(defaultPassword))
                .roles("SYSTEM");
    }

}
