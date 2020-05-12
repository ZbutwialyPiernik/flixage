package com.zbutwialypiernik.flixage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Class needed to create dynamic links to resources, using gateway host
 * Will be mostly used in links to thumbnails
 */
@Component
public class GatewayUriBuilder {

    private final UriComponentsBuilder builder;

    public GatewayUriBuilder(@Value("${GATEWAY_SERVICE_URL}") String baseUrl) {
        builder = UriComponentsBuilder.newInstance();

        if (baseUrl.toLowerCase().startsWith("https://")) {
            baseUrl = baseUrl.replace("https://", "");
            builder.scheme("https").host(baseUrl);
        } else if (baseUrl.toLowerCase().startsWith("http://")) {
            baseUrl = baseUrl.replace("http://", "");
            builder.scheme("http").host(baseUrl);
        } else {
            throw new IllegalArgumentException("Invalid base URL");
        }
    }

    public UriComponentsBuilder newBuilder() {
        return builder.cloneBuilder();
    }

}
