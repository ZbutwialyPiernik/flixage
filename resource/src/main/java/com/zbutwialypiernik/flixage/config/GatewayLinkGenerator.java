package com.zbutwialypiernik.flixage.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Config that is needed to dynamically create links to resources
 * Will be mostly used in links to thumbnails
 */
@Configuration
public class GatewayLinkGenerator {

    private final String baseUrl;

    public GatewayLinkGenerator(@Value("${GATEWAY_SERVICE_URL}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String generateLink(String path) {
        return baseUrl + path;
    }

}
