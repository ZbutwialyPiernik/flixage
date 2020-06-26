package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DatabaseUserDetails userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/eureka/**").hasRole(Role.ADMIN.name())
                //.anyRequest().hasRole(Role.SYSTEM.name()) TODO: Add authentication for
                .anyRequest().permitAll()
            .and()
                .formLogin().permitAll()
            .and()
                .logout().permitAll();
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
