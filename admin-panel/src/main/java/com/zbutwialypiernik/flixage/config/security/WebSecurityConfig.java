package com.zbutwialypiernik.flixage.config.security;

import com.zbutwialypiernik.flixage.service.UserDetailsServiceImpl;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.config.security.filter.LoggedUserFilter;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestCache().requestCache(new CustomRequestCache())
                .and()
                    .authorizeRequests()
                    .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                        .antMatchers("/" + Routes.HOME).hasRole(Role.USER.name())
                        .antMatchers("/" + Routes.ADMIN + "/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/" + Routes.LOGIN)
                    .failureUrl("/" + Routes.LOGIN)
                    .successHandler(successHandler())
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/" + Routes.HOME)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                "/frontend/**",
                "/webjars/**",
                "/images/**",
                "/frontend-es5/**",
                "/frontend-es6/**",
                "/VAADIN/**"
        );
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Authentication Handler configured to redirect to last visited page after successful login
     * If users lost session, he can quickly go back to site he wanted to visit
     **/
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandlerImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Filter loggedUserFilter() {
        return new LoggedUserFilter();
    }

}
