package com.zbutwialypiernik.flixage.config.security;

import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                .requestCache().requestCache(new CustomRequestCache())
                .and()
                    .authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                        .antMatchers("/" + Routes.HOME).hasRole(Role.USER.name())
                        .antMatchers("/" + Routes.ADMIN, "/" + Routes.ADMIN + "/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .loginPage("/" + Routes.LOGIN)
                        .failureUrl("/" + Routes.LOGIN)
                        .successHandler(successHandler())
                        .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/" + Routes.LOGOUT)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/" + Routes.LOGOUT)).permitAll();
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/VAADIN/**",

                "/robots.txt",

                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",

                "/favicon.ico",
                "/favicon/**",
                "/images/**",
                "/icons/**",

                "/webjars/**",

                "/frontend/**",
                "/frontend-es5/**",
                "/frontend-es6/**"
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

}
