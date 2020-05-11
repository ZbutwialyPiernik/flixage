package com.zbutwialypiernik.flixage;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/authentication/**")
                    .uri("lb://authentication/api/authentication")
                    .id("authentication"))
                .route(r -> r.path("/api/**").and().path("/api/authentication/**").negate()
                     .uri("lb://resource/api")
                     .id("resource"))
                .build();
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf().disable().authorizeExchange()
                .pathMatchers("/api/**").permitAll()
                .anyExchange().denyAll()
                .and()
                .build();
    }

}
