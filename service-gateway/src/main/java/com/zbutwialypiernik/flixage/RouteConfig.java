package com.zbutwialypiernik.flixage;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.cloud.gateway.filter.factory.RewriteLocationResponseHeaderGatewayFilterFactory.StripVersion;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/admin/**")
                    // Prevent redirect to local address
                    .filters(f -> f.rewriteLocationResponseHeader(StripVersion.AS_IN_REQUEST.name(), HttpHeaders.LOCATION, "", ""))
                    .uri("lb://admin-service/admin")
                    .id("admin-service"))
                .route(r -> r.path("/api/authentication/**")
                    .uri("lb://authentication-service/api/authentication")
                    .id("authentication-service"))
                .route(r -> r.path("/api/**").and().path("/api/authentication/**").negate()
                     .uri("lb://resource-service/api")
                     .id("resource-service"))
                .build();
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                    .pathMatchers("/api/**", "/admin/**").permitAll()
                .anyExchange().denyAll()
                .and()
                .build();
    }

}
