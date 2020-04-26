package com.zbutwialypiernik.flixage;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/authentication/**")
                    .uri("lb://authentication-server/authentication")
                    .id("authentication-server"))
                .route(r -> r.path("/api/**")
                     .uri("lb://resource-server/")
                     .id("resource-server"))
                .build();
    }

}
